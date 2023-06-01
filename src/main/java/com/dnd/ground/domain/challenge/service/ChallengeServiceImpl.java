package com.dnd.ground.domain.challenge.service;

import com.dnd.ground.domain.challenge.*;
import com.dnd.ground.domain.challenge.dto.*;
import com.dnd.ground.domain.challenge.repository.ChallengeRepository;
import com.dnd.ground.domain.challenge.repository.UserChallengeRepository;
import com.dnd.ground.domain.exerciseRecord.ExerciseRecord;
import com.dnd.ground.domain.exerciseRecord.Repository.ExerciseRecordRepository;
import com.dnd.ground.domain.exerciseRecord.dto.RankDto;
import com.dnd.ground.domain.matrix.dto.Location;
import com.dnd.ground.domain.matrix.dto.MatrixCond;
import com.dnd.ground.domain.matrix.repository.MatrixRepository;
import com.dnd.ground.domain.matrix.service.RankService;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.dto.UserResponseDto;
import com.dnd.ground.domain.user.repository.UserRepository;
import com.dnd.ground.global.util.RequirementUtil;
import com.dnd.ground.global.exception.*;
import com.dnd.ground.global.notification.dto.NotificationForm;
import com.dnd.ground.global.notification.NotificationMessage;
import com.dnd.ground.global.util.UuidUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 박찬호
 * @description 챌린지와 관련된 서비스의 역할을 분리한 구현체
 * @since 2022-08-03
 * @updated 1.챌린지 상세조회 반환할 때, center(latitude, longitude) 추가. (영역이 가장 많은 운동 기록의 가운데)
 *          2.챌린지 상태변경 시 최대 개수를 넘지 않도록 예외처리
 *          3.회원 탈퇴 API 구현 - 해당 멤버를 삭제된 유저로 변환
 *          4. 회원 탈퇴 API 구현 - 참여 중인 챌린지를 삭제된 유저로 변환
 *          - 2023.05.22 박찬호
 */

@Slf4j
@RequiredArgsConstructor
@Service
public class ChallengeServiceImpl implements ChallengeService {

    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;
    private final UserChallengeRepository userChallengeRepository;
    private final ExerciseRecordRepository exerciseRecordRepository;
    private final RankService rankService;
    private final MatrixRepository matrixRepository;
    private final ApplicationEventPublisher pushNotificationPublisher;
    private static final int MAX_CHALLENGE_COUNT = 3;
    private static final int MAX_CHALLENGE_MEMBER_COUNT = 3;
    private static final ChallengeColor[] color = ChallengeColor.values();
    private static final String NOTI_PARAM_CHALLENGE_UUID = "challenge_uuid";

    /*챌린지 생성*/
    @Transactional
    public ChallengeCreateResponseDto createChallenge(ChallengeCreateRequestDto requestDto) {
        //진행 중인 챌린지 개수를 비롯한 필요한 데이터 조회 및 Validation
        Set<String> members = requestDto.getFriends();
        members.add(requestDto.getNickname()); // 주최자 추가
        Map<User, Long> challengeCountMap = challengeRepository.findUsersJoinChallengeCount(members);

        //조회해온 멤버의 수가 맞지 않는 경우(닉네임이 올바르지 않음)
        if (challengeCountMap.size() != members.size()) {
            List<User> notFoundUsers = challengeCountMap.keySet().stream()
                    .filter(c -> !members.contains(c.getNickname()))
                    .collect(Collectors.toList());
            throw new ChallengeException(ExceptionCodeSet.NOT_FOUND_MEMBER, notFoundUsers);
        }

        User master = userRepository.findByNickname(requestDto.getNickname())
                .orElseThrow(() -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));

        if (requestDto.getStarted().isBefore(LocalDateTime.now())) throw new ChallengeException(ExceptionCodeSet.CHALLENGE_DATE_INVALID);
        if (challengeCountMap.get(master) > MAX_CHALLENGE_COUNT) throw new ChallengeException(ExceptionCodeSet.FAIL_CREATE_CHALLENGE);

        //챌린지 생성
        Challenge challenge = Challenge.create()
                .uuid(UuidUtil.createUUID())
                .name(requestDto.getName())
                .started(requestDto.getStarted())
                .ended(ChallengeService.getSunday(requestDto.getStarted()))
                .message(requestDto.getMessage())
                .type(requestDto.getType())
                .build();

        //챌린지 저장
        challengeRepository.save(challenge);

        //챌린지 참여하는 관계 생성을 위한 변수
        int exceptMemberCount = 0;
        List<String> exceptMembers = new ArrayList<>();
        List<UserResponseDto.UInfo> membersInfo = new ArrayList<>();

        //주최자 관계 생성
        int colorIdx = (int) (challengeCountMap.get(master) % MAX_CHALLENGE_COUNT);
        userChallengeRepository.save(new UserChallenge(challenge, master, color[colorIdx], ChallengeStatus.MASTER));
        challengeCountMap.remove(master);

        //멤버 관계 생성
        for (Map.Entry<User, Long> entry : challengeCountMap.entrySet()) {
            User member = entry.getKey();
            if (entry.getValue() <= MAX_CHALLENGE_COUNT) {
                colorIdx = (int) (challengeCountMap.get(member) % MAX_CHALLENGE_COUNT);
                userChallengeRepository.save(new UserChallenge(challenge, member, color[colorIdx], ChallengeStatus.WAIT));
                membersInfo.add(new UserResponseDto.UInfo(member.getNickname(), member.getPicturePath()));
            } else {
                exceptMembers.add(member.getNickname());
                exceptMemberCount++;
            }
        }

        //챌린지는 혼자 진행할 수 없음.
        if (membersInfo.size() == 0) throw new ChallengeException(ExceptionCodeSet.NOT_ALONE_CHALLENGE);

        //푸시 알람 발송
        pushNotificationPublisher.publishEvent(
                new NotificationForm(
                        new ArrayList<>(challengeCountMap.keySet()),
                        List.of(master.getNickname()),
                        List.of(challenge.getName()),
                        NotificationMessage.CHALLENGE_RECEIVED_REQUEST,
                        new HashMap<>(){{
                            put(NOTI_PARAM_CHALLENGE_UUID, UuidUtil.bytesToHex(challenge.getUuid()));
                        }}
                )
        );

        return ChallengeCreateResponseDto.builder()
                .members(membersInfo)
                .message(challenge.getMessage())
                .started(challenge.getStarted())
                .ended(ChallengeService.getSunday(challenge.getStarted()))
                .exceptMemberCount(exceptMemberCount)
                .exceptMembers(exceptMembers)
                .build();
    }

    /*유저-챌린지 상태 변경*/
    @Transactional
    public ChallengeResponseDto.Status changeUserChallengeStatus(ChallengeRequestDto.CInfo requestDto, ChallengeStatus status) {
        User user = userRepository.findByNickname(requestDto.getNickname())
                .orElseThrow(() -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));
        UserChallenge userChallenge = challengeRepository.findUC(requestDto.getNickname(), requestDto.getUuid());

        if (userChallenge == null) throw new ChallengeException(ExceptionCodeSet.NOT_FOUND_UC);
        else if (userChallenge.getStatus() == ChallengeStatus.MASTER) { //주최자의 상태 변경X
            throw new ChallengeException(ExceptionCodeSet.MASTER_STATUS_NOT_CHANGE, requestDto.getNickname());
        }

        if (status == ChallengeStatus.READY && MAX_CHALLENGE_COUNT < challengeRepository.findUserJoinChallengeCount(user)) {
            throw new ChallengeException(ExceptionCodeSet.CHALLENGE_EXCEED);
        }

        //상태 변경
        userChallenge.changeStatus(status);

        //푸시 알람 발송
        if (status.equals(ChallengeStatus.READY)) {
            User master = userChallengeRepository.findMasterInChallenge(UuidUtil.hexToBytes(requestDto.getUuid()));
            pushNotificationPublisher.publishEvent(
                    new NotificationForm(
                            new ArrayList<>(Arrays.asList(master)),
                            List.of(user.getNickname()),
                            List.of(user.getNickname()),
                            NotificationMessage.CHALLENGE_ACCEPTED,
                            new HashMap<>(){{
                                put(NOTI_PARAM_CHALLENGE_UUID, requestDto.getUuid());
                            }}
                    )
            );
        }

        return new ChallengeResponseDto.Status(status);
    }

    /*초대 받은 챌린지 목록 조회*/
    public List<ChallengeResponseDto.Invite> findInviteChallenge(String nickname) {
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));

        List<Challenge> challenges = challengeRepository.findChallengesByCond(new ChallengeCond(user, List.of(ChallengeStatus.WAIT)));
        List<ChallengeResponseDto.Invite> response = new ArrayList<>();

        for (Challenge challenge : challenges) {
            User master = userChallengeRepository.findMasterInChallenge(challenge);

            response.add(
                    ChallengeResponseDto.Invite.builder()
                            .name(challenge.getName())
                            .uuid(UuidUtil.bytesToHex(challenge.getUuid()))
                            .InviterNickname(master.getNickname())
                            .message(challenge.getMessage())
                            .created(challenge.getCreated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                            .picturePath(master.getPicturePath())
                            .build()
            );
        }

        return response;
    }

    /*진행 대기 중인 챌린지 리스트 조회*/
    public List<ChallengeResponseDto.Wait> findWaitChallenge(String nickname) {
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));

        Map<Challenge, List<UCDto.UCInfo>> challengesInfo = challengeRepository.findUCInChallenge(new ChallengeCond(user, List.of(ChallengeStatus.READY, ChallengeStatus.MASTER)));
        Map<Challenge, ChallengeColor> colorInfo = challengeRepository.findChallengesColor(new ChallengeCond(user, List.of(ChallengeStatus.READY, ChallengeStatus.MASTER)));

        List<ChallengeResponseDto.Wait> response = new ArrayList<>();

        for (Map.Entry<Challenge, List<UCDto.UCInfo>> entry : challengesInfo.entrySet()) {
            Challenge challenge = entry.getKey();
            List<UCDto.UCInfo> ucInfo = entry.getValue();
            List<String> pictures = new ArrayList<>();
            int memberCount = 0;
            int readyCount = 0;

            for (UCDto.UCInfo uc : ucInfo) {
                pictures.add(uc.getPicturePath());
                if (uc.getStatus().equals(ChallengeStatus.READY) || uc.getStatus().equals(ChallengeStatus.MASTER))
                    readyCount++;

                memberCount++;
            }

            response.add(
                    ChallengeResponseDto.Wait.builder()
                            .name(challenge.getName())
                            .uuid(UuidUtil.bytesToHex(challenge.getUuid()))
                            .started(challenge.getStarted())
                            .ended(challenge.getEnded())
                            .totalCount(memberCount)
                            .readyCount(readyCount)
                            .color(colorInfo.get(challenge))
                            .picturePaths(pictures)
                            .build()
            );
        }
        return response;
    }

    /*진행 중인 챌린지 리스트 조회*/
    public List<ChallengeResponseDto.Progress> findProgressChallenge(String nickname) {
        User user = userRepository.findByNickname(nickname).orElseThrow(
                () -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));

        List<ChallengeResponseDto.Progress> response = new ArrayList<>();
        Map<Challenge, List<RankDto>> challengeMatrixRank = exerciseRecordRepository.findChallengeMatrixRank(user, List.of(ChallengeStatus.MASTER_PROGRESS, ChallengeStatus.PROGRESS));
        Map<Challenge, ChallengeColor> colors = challengeRepository.findChallengesColor(new ChallengeCond(user, List.of(ChallengeStatus.MASTER_PROGRESS, ChallengeStatus.PROGRESS)));

        for (Map.Entry<Challenge, List<RankDto>> entry : challengeMatrixRank.entrySet()) {
            Challenge challenge = entry.getKey();
            List<RankDto> rankInfo = entry.getValue();

            List<String> pictures = rankInfo.stream()
                    .map(RankDto::getPicturePath)
                    .collect(Collectors.toList());

            response.add(
                    ChallengeResponseDto.Progress.builder()
                            .name(challenge.getName())
                            .uuid(UuidUtil.bytesToHex(challenge.getUuid()))
                            .started(challenge.getStarted())
                            .ended(challenge.getEnded())
                            .rank(rankService.calculateUserRank(rankInfo, user).getRank())
                            .color(colors.get(challenge))
                            .picturePaths(pictures)
                            .build()
            );
        }

        return response;
    }

    /*친구와 함께 진행 중인 챌린지 리스트 조회*/
    public List<ChallengeResponseDto.Progress> findProgressChallenge(String userNickname, String friendNickname) {
        User user = userRepository.findByNickname(userNickname).orElseThrow(
                () -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));

        User friend = userRepository.findByNickname(friendNickname).orElseThrow(
                () -> new FriendException(ExceptionCodeSet.FRIEND_NOT_FOUND));

        Map<Challenge, List<RankDto>> challengesWithFriend = exerciseRecordRepository.findChallengeMatrixRankWithUsers(user, List.of(friend), List.of(ChallengeStatus.MASTER_PROGRESS, ChallengeStatus.PROGRESS));
        Map<Challenge, ChallengeColor> colors = challengeRepository.findChallengesColor(new ChallengeCond(user, List.of(ChallengeStatus.MASTER_PROGRESS, ChallengeStatus.PROGRESS)));
        List<ChallengeResponseDto.Progress> response = new ArrayList<>();

        for (Map.Entry<Challenge, List<RankDto>> entry : challengesWithFriend.entrySet()) {
            Challenge challenge = entry.getKey();

            response.add(
                    ChallengeResponseDto.Progress.builder()
                            .name(challenge.getName())
                            .uuid(UuidUtil.bytesToHex(challenge.getUuid()))
                            .started(challenge.getStarted())
                            .ended(challenge.getEnded())
                            .rank(rankService.calculateUserRank(entry.getValue(), friend).getRank())
                            .color(colors.get(challenge))
                            .build()
            );
        }

        return response;
    }

    /*진행 완료된 챌린지 리스트 조회*/
    public List<ChallengeResponseDto.Done> findDoneChallenge(String nickname) {
        User user = userRepository.findByNickname(nickname).orElseThrow(
                () -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));

        List<ChallengeResponseDto.Done> response = new ArrayList<>();

        Map<Challenge, List<RankDto>> challengeMatrixRank = exerciseRecordRepository.findChallengeMatrixRank(user, List.of(ChallengeStatus.MASTER_DONE, ChallengeStatus.DONE));
        Map<Challenge, ChallengeColor> colors = challengeRepository.findChallengesColor(new ChallengeCond(user, List.of(ChallengeStatus.MASTER_DONE, ChallengeStatus.DONE)));

        for (Map.Entry<Challenge, List<RankDto>> entry : challengeMatrixRank.entrySet()) {
            Challenge challenge = entry.getKey();
            List<RankDto> rankInfo = entry.getValue();

            List<String> pictures = rankInfo.stream()
                    .map(RankDto::getPicturePath)
                    .collect(Collectors.toList());

            response.add(
                    ChallengeResponseDto.Done.builder()
                            .name(challenge.getName())
                            .uuid(UuidUtil.bytesToHex(challenge.getUuid()))
                            .started(challenge.getStarted())
                            .ended(challenge.getEnded())
                            .rank(rankService.calculateUserRank(rankInfo, user).getRank())
                            .color(colors.get(challenge))
                            .picturePaths(pictures)
                            .build()
            );
        }

        return response;
    }

    /*진행 대기 중 챌린지 상세 조회*/
    public ChallengeResponseDto.WaitDetail getDetailWaitChallenge(ChallengeRequestDto.CInfo request) {
        User user = userRepository.findByNickname(request.getNickname())
                .orElseThrow(() -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));

        Challenge challenge = challengeRepository.findByUuid(UuidUtil.hexToBytes(request.getUuid()))
                .orElseThrow(() -> new ChallengeException(ExceptionCodeSet.CHALLENGE_NOT_FOUND));

        Map<Challenge, ChallengeColor> colors = challengeRepository.findChallengesColor(new ChallengeCond(user, List.of(ChallengeStatus.WAIT)));
        List<UserChallenge> ucList = userChallengeRepository.findByChallenge(challenge);

        List<UCDto.UCInfo> infos = new ArrayList<>();
        for (UserChallenge uc : ucList) {
            User userInUC = uc.getUser();
            UCDto.UCInfo ucInfo = new UCDto.UCInfo(userInUC.getPicturePath(), userInUC.getNickname(), uc.getStatus());

            if (uc.getStatus() == ChallengeStatus.MASTER) {
                infos.add(0, ucInfo);
            } else if (userInUC.getNickname().equals(user.getNickname())) {
                infos.add(1, ucInfo);
            } else {
                infos.add(ucInfo);
            }
        }

        return ChallengeResponseDto.WaitDetail.builder()
                .name(challenge.getName())
                .type(challenge.getType())
                .color(colors.get(challenge))
                .started(challenge.getStarted())
                .ended(challenge.getEnded())
                .infos(infos)
                .build();
    }

    /*진행 중, 완료된 챌린지 상세 조회*/
    public ChallengeResponseDto.Detail getDetailProgressOrDone(ChallengeRequestDto.CInfo request) {
        User user = userRepository.findByNickname(request.getNickname())
                .orElseThrow(() -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));

        Challenge challenge = challengeRepository.findByUuid(UuidUtil.hexToBytes(request.getUuid()))
                .orElseThrow(() -> new ChallengeException(ExceptionCodeSet.CHALLENGE_NOT_FOUND));

        //개인 기록 계산 및 영역 저장
        LocalDateTime started = challenge.getStarted();
        LocalDateTime ended = challenge.getEnded();
        int distance = 0;
        int exerciseTime = 0;
        int stepCount = 0;
        Double latitude = null;
        Double longitude = null;

        Map<ExerciseRecord, List<Location>> recordWithLocation = exerciseRecordRepository.findRecordWithLocation(user, started, ended);
        List<Location> matrices = new ArrayList<>();
        int biggestSize = Integer.MIN_VALUE;

        for (Map.Entry<ExerciseRecord, List<Location>> entry : recordWithLocation.entrySet()) {
            ExerciseRecord record = entry.getKey();
            List<Location> locations = entry.getValue();

            distance += record.getDistance();
            exerciseTime += record.getExerciseTime();
            stepCount += record.getStepCount();

            matrices.addAll(locations);

            if (biggestSize < locations.size()) {
                biggestSize = locations.size();
                int center = locations.size() / 2;

                latitude = locations.get(center).getLatitude();
                longitude = locations.get(center).getLongitude();
            }
        }

        //랭킹 계산
        List<User> members = userChallengeRepository.findMembers(challenge); //본인 포함 챌린지에 참여하는 인원들
        if (!members.contains(user)) throw new ChallengeException(ExceptionCodeSet.USER_CHALLENGE_NOT_FOUND); //참여하는 챌린지가 아니면 예외처리

        Map<Challenge, List<RankDto>> challengeMatrixRankWithMembers = exerciseRecordRepository.findChallengeMatrixRankWithUsers(user, members, List.of(ChallengeStatus.PROGRESS, ChallengeStatus.DONE));
        List<UserResponseDto.Ranking> rankings = rankService.calculateUsersRank(challengeMatrixRankWithMembers.get(challenge));

        return ChallengeResponseDto.Detail.builder()
                .name(challenge.getName())
                .uuid(UuidUtil.bytesToHex(challenge.getUuid()))
                .type(challenge.getType())
                .started(started)
                .ended(ended)
                .color(userChallengeRepository.findChallengeColor(user, challenge))
                .matrices(matrices)
                .rankings(rankings)
                .distance(distance)
                .exerciseTime(exerciseTime)
                .stepCount(stepCount)
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }

    /*해당 운동기록이 참여하고 있는 챌린지*/
    public List<ChallengeResponseDto.CInfoRes> findChallengeByRecord(ExerciseRecord exerciseRecord) {
        User user = userRepository.findByExerciseRecord(exerciseRecord)
                .orElseThrow(() -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));

        // 해당주 월요일 ~ 기록 시간 사이 시작한 챌린지들 조회
        List<Challenge> challenges = challengeRepository.findChallengesByCond(new ChallengeCond(user, exerciseRecord.getStarted(), exerciseRecord.getEnded()));
        Map<Challenge, ChallengeColor> colors = challengeRepository.findChallengesColor(new ChallengeCond(user));
        List<ChallengeResponseDto.CInfoRes> cInfoRes = new ArrayList<>();

        challenges.forEach(c -> cInfoRes.add(
                ChallengeResponseDto.CInfoRes.builder()
                        .name(c.getName())
                        .uuid(UuidUtil.bytesToHex(c.getUuid()))
                        .started(c.getStarted())
                        .ended(c.getEnded())
                        .color(colors.get(c))
                        .build()
                )
        );

        return cInfoRes;
    }

    /*챌린지 상세보기: 지도*/
    public ChallengeMapResponseDto.DetailMap getChallengeDetailMap(String uuid, String nickname, Double spanDelta, Location center) {
        Challenge challenge = challengeRepository.findByUuid(UuidUtil.hexToBytes(uuid))
                .orElseThrow(() -> new ChallengeException(ExceptionCodeSet.CHALLENGE_NOT_FOUND));

        //챌린지 참여 인원 조회
        List<User> members = userChallengeRepository.findMembers(challenge);

        //랭킹 계산
        List<RankDto> rankByChallenge = exerciseRecordRepository.findRankByChallenge(challenge);
        List<UserResponseDto.Ranking> rankings = rankService.calculateUsersRank(rankByChallenge);

        members.sort(Comparator.comparing(User::getNickname));
        rankings.sort(Comparator.comparing(UserResponseDto.Ranking::getNickname));

        //영역 조회
        List<ChallengeMapResponseDto.UserMapInfo> matrixList = new ArrayList<>();

        int i=0;
        for (User member : members) {
            List<Location> matrices = matrixRepository.findMatrixList(new MatrixCond(member, challenge.getStarted(), challenge.getEnded(), spanDelta, center));
            if (member.getNickname().equals(nickname)) {
                matrixList.add(0,
                        new ChallengeMapResponseDto.UserMapInfo(nickname, color[MAX_CHALLENGE_MEMBER_COUNT], member.getLatitude(), member.getLongitude(), matrices, member.getPicturePath())
                );
            } else {
                matrixList.add(
                        new ChallengeMapResponseDto.UserMapInfo(member.getNickname(), color[i], member.getLatitude(), member.getLongitude(), matrices, member.getPicturePath())
                );
                i++;
            }
        }

        return new ChallengeMapResponseDto.DetailMap(matrixList, rankings);
    }

    /*챌린지 삭제*/
    public Boolean deleteChallenge(ChallengeRequestDto.CInfo request) {
        User user = userRepository.findByNickname(request.getNickname()).orElseThrow(
                () -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));

        Challenge challenge = challengeRepository.findByUuid(UuidUtil.hexToBytes(request.getUuid()))
                .orElseThrow(() -> new ChallengeException(ExceptionCodeSet.CHALLENGE_NOT_FOUND, user.getNickname()));

        UserChallenge userChallenge = userChallengeRepository.findByUserAndChallenge(user, challenge)
                .orElseThrow(() -> new ChallengeException(ExceptionCodeSet.USER_CHALLENGE_NOT_FOUND, user.getNickname()));

        //주최자만 삭제 가능.
        if (userChallenge.getStatus() != ChallengeStatus.MASTER) {
            return false;
        } else {
            List<UserChallenge> userChallenges = userChallengeRepository.findByChallenge(challenge);
            userChallengeRepository.deleteAll(userChallenges);
            challengeRepository.delete(challenge);
            return true;
        }
    }

    /**
     * 회원 탈퇴의 경우, 1명만 남아도 챌린지가 진행된다는 요구사항에 따라 (알 수 없음) 회원으로 변경한다.
     * 모든 회원이 탈퇴하면 챌린지가 삭제된다.
     * @param user
     */
    @Override
    @Transactional
    public void convertDeleteUser(User user) {
        List<ChallengeUcsDto> challengeUcs = challengeRepository.findChallengeUcs(user);
        User deleteUser = RequirementUtil.getDeleteUser();
        user.clearChallenges();

        for (ChallengeUcsDto challengeUc : challengeUcs) {
            Challenge challenge = challengeUc.getChallenge();
            List<UserChallenge> ucs = challengeUc.getUcs();

            int cnt = 1;
            for (UserChallenge uc : ucs) {
                User member = uc.getUser();

                if (Objects.equals(member,user)) {
                    uc.changeUser(deleteUser);
                } else if (Objects.equals(member.getId(), deleteUser.getId())) {
                    cnt++;
                }
            }

            if (ucs.size() == 1 || cnt == ucs.size()) {
                userChallengeRepository.deleteAll(ucs);
                challengeRepository.delete(challenge);
            }
        }
    }
}