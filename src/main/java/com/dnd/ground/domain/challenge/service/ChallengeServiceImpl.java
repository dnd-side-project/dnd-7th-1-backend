package com.dnd.ground.domain.challenge.service;

import com.dnd.ground.domain.challenge.*;
import com.dnd.ground.domain.challenge.dto.*;
import com.dnd.ground.domain.challenge.repository.ChallengeRepository;
import com.dnd.ground.domain.challenge.repository.UserChallengeRepository;
import com.dnd.ground.domain.exerciseRecord.ExerciseRecord;
import com.dnd.ground.domain.exerciseRecord.Repository.ExerciseRecordRepository;
import com.dnd.ground.domain.exerciseRecord.dto.RankDto;
import com.dnd.ground.domain.matrix.dto.MatrixDto;
import com.dnd.ground.domain.matrix.repository.MatrixRepository;
import com.dnd.ground.domain.matrix.service.RankService;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.dto.RankResponseDto;
import com.dnd.ground.domain.user.dto.UserResponseDto;
import com.dnd.ground.domain.user.repository.UserRepository;
import com.dnd.ground.global.exception.*;
import com.dnd.ground.global.util.UuidUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Tuple;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 박찬호
 * @description 챌린지와 관련된 서비스의 역할을 분리한 구현체
 * @since 2022-08-03
 * @updated 1. 진행 완료된 챌린지 리팩토링
 *          - 2023.02.28
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
    private static final int MAX_CHALLENGE_COUNT = 3;

    /*챌린지 생성*/
    @Transactional
    public ChallengeCreateResponseDto createChallenge(ChallengeCreateRequestDto requestDto) {
        //진행 중인 챌린지 개수를 비롯한 필요한 데이터 조회 및 Validation
        Set<String> members = requestDto.getFriends();
        members.add(requestDto.getNickname()); // 주최자 추가
        Map<User, Long> challengeCountMap = challengeRepository.findUsersProgressChallengeCount(members);

        //조회해온 멤버의 수가 맞지 않는 경우(닉네임이 올바르지 않음)
        if (challengeCountMap.size() != members.size()) {
            List<User> notFoundUsers = challengeCountMap.keySet().stream()
                    .filter(c -> !members.contains(c.getNickname()))
                    .collect(Collectors.toList());
            throw new ChallengeException(ExceptionCodeSet.NOT_FOUND_MEMBER, notFoundUsers);
        }

        User master = userRepository.findByNickname(requestDto.getNickname()).orElseThrow(
                () -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));

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
        ChallengeColor[] color = ChallengeColor.values();
        int exceptMemberCount = 0;
        List<String> exceptMembers = new ArrayList<>();
        List<UserResponseDto.UInfo> membersInfo = new ArrayList<>();

        //주최자 관계 생성
        int colorIdx = (int) (challengeCountMap.get(master)%MAX_CHALLENGE_COUNT);
        userChallengeRepository.save(new UserChallenge(challenge, master, color[colorIdx], ChallengeStatus.MASTER));
        challengeCountMap.remove(master);

        //멤버 관계 생성
        for (Map.Entry<User, Long> memberEntry : Set.copyOf(challengeCountMap.entrySet())) {
            User member = memberEntry.getKey();
            if (memberEntry.getValue() <= MAX_CHALLENGE_COUNT) {
                colorIdx = (int) (challengeCountMap.get(member)%MAX_CHALLENGE_COUNT);
                userChallengeRepository.save(new UserChallenge(challenge, member, color[colorIdx], ChallengeStatus.WAIT));
                membersInfo.add(new UserResponseDto.UInfo(member.getNickname(), member.getPicturePath()));
            } else {
                challengeCountMap.remove(member);
                exceptMembers.add(member.getNickname());
                exceptMemberCount++;
            }
        }

        //챌린지는 혼자 진행할 수 없음.
        if (membersInfo.size() == 0) throw new ChallengeException(ExceptionCodeSet.NOT_ALONE_CHALLENGE);

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
        //정보 조회
        Challenge challenge = challengeRepository.findByUuid(requestDto.getUuid()).orElseThrow(
                () -> new ChallengeException(ExceptionCodeSet.CHALLENGE_NOT_FOUND));

        User user = userRepository.findByNickname(requestDto.getNickname()).orElseThrow(
                () -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));

        UserChallenge userChallenge = userChallengeRepository.findByUserAndChallenge(user, challenge).orElseThrow(
                () -> new ChallengeException(ExceptionCodeSet.USER_CHALLENGE_NOT_FOUND, user.getNickname()));

        //주최자의 상태 변경X
        if (userChallenge.getStatus() == ChallengeStatus.MASTER) {
            throw new ChallengeException(ExceptionCodeSet.MASTER_STATUS_NOT_CHANGE, user.getNickname());
        }

        //상태 변경
        userChallenge.changeStatus(status);

        return new ChallengeResponseDto.Status(status);
    }

    /*초대 받은 챌린지 목록 조회*/
    public List<ChallengeResponseDto.Invite> findInviteChallenge(String nickname) {
        User user = userRepository.findByNickname(nickname).orElseThrow(
                () -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));

        List<Challenge> challenges = challengeRepository.findChallengesByUserInStatus(user, ChallengeStatus.WAIT);
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
        User user = userRepository.findByNickname(nickname).orElseThrow(
                () -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));

        List<Challenge> challenges = challengeRepository.findChallengesByUserInStatus(user, ChallengeStatus.WAIT);
        List<ChallengeResponseDto.Wait> response = new ArrayList<>();

        Map<Challenge, ChallengeColor> colorInfo = challengeRepository.findChallengesColor(user, ChallengeStatus.WAIT);

        for (Challenge challenge : challenges) {
            LocalDateTime started = challenge.getStarted();
            List<String> picturePaths = new ArrayList<>();  // 유저의 프로필 사진

            userChallengeRepository.findChallengeUsers(challenge)
                    .forEach(u -> picturePaths.add(u.getPicturePath()));

            response.add(
                    ChallengeResponseDto.Wait.builder()
                            .name(challenge.getName())
                            .uuid(new String(challenge.getUuid()))
                            .started(started)
                            .ended(ChallengeService.getSunday(started))
                            .totalCount(userChallengeRepository.findUCCount(challenge)) //챌린지에 참여하는 전체 인원 수
                            .readyCount(userChallengeRepository.findUCReadyCount(challenge)) //Progress 상태 회원 수 + 주최자
                            .color(colorInfo.get(challenge))
                            .picturePaths(picturePaths)
                            .build()
            );
        }

        return response;
    }

    /*진행 중인 챌린지 리스트 조회*/
    public List<ChallengeResponseDto.Progress> findProgressChallenge(String nickname) {
        User user = userRepository.findByNickname(nickname).orElseThrow(
                () -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));

        List<Challenge> progressChallenge = challengeRepository.findProgressChallenge(user);
        List<ChallengeResponseDto.Progress> response = new ArrayList<>();

        for (Challenge challenge : progressChallenge) {
            Integer rank = -1; //랭킹
            LocalDateTime started = challenge.getStarted(); //챌린지 시작 날짜
            List<String> picturePaths = new ArrayList<>(); // 유저들의 프로필 사진

            //해당 회원의 랭킹 추출
            if (challenge.getType() == ChallengeType.WIDEN) {
                RankResponseDto.Area rankList = rankService.challengeRank(challenge, started, LocalDateTime.now());

                for (UserResponseDto.Ranking ranking : rankList.getAreaRankings()) {
                    if (ranking.getNickname().equals(nickname)) {
                        rank = ranking.getRank();
                    }
                    picturePaths.add(ranking.getPicturePath());
                }
            } else if (challenge.getType() == ChallengeType.ACCUMULATE) {
                //챌린지를 함께 진행하는 회원 목록
                List<User> member = userChallengeRepository.findChallengeUsers(challenge);
                //기록 조회
                List<Tuple> matrixCount = exerciseRecordRepository.findMatrixCount(member, started, LocalDateTime.now());
                //랭킹 계산
                for (UserResponseDto.Ranking ranking : rankService.calculateMatrixRank(matrixCount, member)) {
                    if (ranking.getNickname().equals(nickname)) {
                        rank = ranking.getRank();
                    }
                    picturePaths.add(ranking.getPicturePath());
                }
            }

            //랭킹이 -1인 경우 예외 처리
            if (rank == -1) {
                throw new ExerciseRecordException(ExceptionCodeSet.RANKING_CAL_FAIL);
            }

            response.add(
                    ChallengeResponseDto.Progress.builder()
                            .name(challenge.getName())
                            .uuid(new String(challenge.getUuid()))
                            .started(started)
                            .ended(ChallengeService.getSunday(started))
                            .rank(rank)
                            .color(userChallengeRepository.findChallengeColor(user, challenge))
                            .picturePaths(picturePaths)
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

        List<Challenge> progressChallenge = challengeRepository.findChallengesWithFriend(user, friend);
        List<ChallengeResponseDto.Progress> response = new ArrayList<>();

        for (Challenge challenge : progressChallenge) {
            Integer rank = -1; //랭킹
            LocalDateTime started = challenge.getStarted(); //챌린지 시작 날짜

            //해당 회원(친구)의 랭킹 추출
            if (challenge.getType() == ChallengeType.WIDEN) {
                RankResponseDto.Area rankList = rankService.challengeRank(challenge, started, LocalDateTime.now());

                for (UserResponseDto.Ranking ranking : rankList.getAreaRankings()) {
                    if (ranking.getNickname().equals(friendNickname)) {
                        rank = ranking.getRank();
                        break;
                    }
                }
            } else if (challenge.getType() == ChallengeType.ACCUMULATE) {
                //챌린지를 함께 진행하는 회원 목록
                List<User> member = userChallengeRepository.findChallengeUsers(challenge);
                //기록 조회
                List<Tuple> matrixCount = exerciseRecordRepository.findMatrixCount(member, started, LocalDateTime.now());
                //랭킹 계산
                for (UserResponseDto.Ranking ranking : rankService.calculateMatrixRank(matrixCount, member)) {
                    if (ranking.getNickname().equals(friendNickname)) {
                        rank = ranking.getRank();
                        break;
                    }
                }
            }

            //랭킹이 -1인 경우 예외 처리
            if (rank == -1) {
                throw new ExerciseRecordException(ExceptionCodeSet.RANKING_CAL_FAIL);
            }

            response.add(
                    ChallengeResponseDto.Progress.builder()
                            .name(challenge.getName())
                            .uuid(new String(challenge.getUuid()))
                            .started(started)
                            .ended(started.plusDays(7 - started.getDayOfWeek().getValue()))
                            .rank(rank)
                            .color(userChallengeRepository.findChallengeColor(user, challenge))
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

        Map<Challenge, List<RankDto>> challengeMatrixRank = exerciseRecordRepository.findChallengeMatrixRank(user, ChallengeStatus.DONE);
        Map<Challenge, ChallengeColor> colors = challengeRepository.findChallengesColor(user, ChallengeStatus.DONE);

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
        User user = userRepository.findByNickname(request.getNickname()).orElseThrow(
                () -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));

        Challenge challenge = challengeRepository.findByUuid(request.getUuid()).orElseThrow(
                () -> new ChallengeException(ExceptionCodeSet.CHALLENGE_NOT_FOUND));

        LocalDateTime started = challenge.getStarted(); //챌린지 시작 날짜
        LocalDateTime ended = challenge.getEnded();

        List<UserChallenge> ucList = userChallengeRepository.findUCByChallenge(challenge);
        List<UCDto.UCInfo> infos = new ArrayList<>();

        for (UserChallenge uc : ucList) {
            User userInUC = uc.getUser();
            UCDto.UCInfo ucInfo = new UCDto.UCInfo(userInUC.getPictureName(), userInUC.getNickname(), uc.getStatus());

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
                .color(userChallengeRepository.findChallengeColor(user, challenge))
                .started(started)
                .ended(ended)
                .infos(infos)
                .build();
    }

    /*진행 중인 챌린지 상세 조회*/
    public ChallengeResponseDto.ProgressDetail getDetailProgress(ChallengeRequestDto.CInfo request) {
        User user = userRepository.findByNickname(request.getNickname()).orElseThrow(
                () -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));

        Challenge challenge = challengeRepository.findByUuid(request.getUuid()).orElseThrow(
                () -> new ChallengeException(ExceptionCodeSet.CHALLENGE_NOT_FOUND));

        //필요한 변수 선언
        List<User> members = userChallengeRepository.findChallengeUsers(challenge); //본인 포함 챌린지에 참여하는 인원들

        //참여하는 챌린지가 아니면 예외처리
        if (!members.contains(user)) throw new ChallengeException(ExceptionCodeSet.USER_CHALLENGE_NOT_FOUND);

        List<MatrixDto> matrices; //영역 기록
        List<UserResponseDto.Ranking> rankings = new ArrayList<>(); //랭킹
        List<ExerciseRecord> records; //영역 기록

        LocalDateTime started = challenge.getStarted(); //챌린지 시작 날짜
        LocalDateTime ended = challenge.getEnded(); //챌린지 끝나는 날(해당 주 일요일)

        Integer distance = 0; //거리
        Integer exerciseTime = 0; //운동시간
        Integer stepCount = 0; //걸음수

        //개인 기록 계산
        records = exerciseRecordRepository.findRecord(user.getId(), started, ended);

        for (ExerciseRecord record : records) {
            distance += record.getDistance();
            exerciseTime += record.getExerciseTime();
            stepCount += record.getStepCount();
        }

        //영역 정보 조회
        matrices = matrixRepository.findMatrixSetByRecords(records);

        //챌린지 타입에 따른 랭킹 정보(순위, 닉네임, 점수) 계산
        rankings = calculateChallengeRanking(challenge, members, started.toLocalDate(), ended.toLocalDate(), challenge.getType());

        return ChallengeResponseDto.ProgressDetail.builder()
                .name(challenge.getName())
                .uuid(new String(challenge.getUuid()))
                .type(challenge.getType())
                .started(started)
                .ended(ChallengeService.getSunday(started))
                .color(userChallengeRepository.findChallengeColor(user, challenge))
                .matrices(matrices)
                .rankings(rankings)
                .distance(distance)
                .exerciseTime(exerciseTime)
                .stepCount(stepCount)
                .build();
    }

    /*해당 운동기록이 참여하고 있는 챌린지*/
    public List<ChallengeResponseDto.CInfoRes> findChallengeByRecord(ExerciseRecord exerciseRecord) {

        User user = userRepository.findByExerciseRecord(exerciseRecord).orElseThrow(
                () -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));

        // LocalDate 형태로 변환
        LocalDateTime startedTime = exerciseRecord.getStarted();
        LocalDate startedDate = LocalDate.of(startedTime.getYear(), startedTime.getMonth(), startedTime.getDayOfMonth());
        LocalDate monday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        // 해당주 월요일 ~ 기록 시간 사이 시작한 챌린지들 조회
        List<Challenge> challenges = challengeRepository.findChallengesBetweenStartAndEnd(user, monday, startedDate);
        List<ChallengeResponseDto.CInfoRes> cInfoRes = new ArrayList<>();

        challenges.forEach(c -> cInfoRes.add(ChallengeResponseDto.CInfoRes.builder()
                .name(c.getName())
                .uuid(new String(c.getUuid()))
                .started(c.getStarted())
                .ended(c.getStarted().plusDays(7 - c.getStarted().getDayOfWeek().getValue()))
                .color(userChallengeRepository.findChallengeColor(user, c))
                .build()));

        return cInfoRes;
    }

    /*챌린지 상세보기: 지도*/
    public ChallengeMapResponseDto.Detail getChallengeDetailMap(String uuid) {
        Challenge challenge = challengeRepository.findByUuid(uuid).orElseThrow(
                () -> new ChallengeException(ExceptionCodeSet.CHALLENGE_NOT_FOUND));

        //챌린지 참여 인원 조회
        List<User> members = userChallengeRepository.findChallengeUsers(challenge);

        //필요한 변수 선언
        List<ChallengeMapResponseDto.UserMapInfo> matrixList = new ArrayList<>();
        List<UserResponseDto.Ranking> rankings = new ArrayList<>();
        List<ExerciseRecord> records;

        LocalDateTime started = challenge.getStarted(); //챌린지 시작 날짜
        LocalDateTime ended = challenge.getEnded(); //챌린지 끝나는 날(해당 주 일요일)

        //챌린지 타입에 따른 결과 저장
        if (challenge.getType().equals(ChallengeType.WIDEN)) {
            for (User member : members) {
                ChallengeColor color = userChallengeRepository.findChallengeColor(member, challenge);

                //각 유저의 챌린지 기간동안의 기록
                records = exerciseRecordRepository.findRecord(member.getId(), started, ended);
                //개인 영역 기록 저장
                List<MatrixDto> matrixSetByRecord = matrixRepository.findMatrixSetByRecords(records);
                matrixList.add(
                        new ChallengeMapResponseDto.UserMapInfo(color, member.getLatitude(), member.getLongitude(), matrixSetByRecord, member.getPicturePath())
                );

                //랭킹 리스트에 추가
                rankings.add(new UserResponseDto.Ranking(1, member.getNickname(),
                        (long) matrixRepository.findMatrixSetByRecords(records).size(), member.getPicturePath()));
            }
            //랭킹 정렬
            rankings = rankService.calculateAreaRank(rankings);
        } else if (challenge.getType().equals(ChallengeType.ACCUMULATE)) {
            for (User user : members) {
                //matrixList 값 채우기
                ChallengeColor color = userChallengeRepository.findChallengeColor(user, challenge);

                //개인 기록 계산
                records = exerciseRecordRepository.findRecord(user.getId(), started, ended);
                List<MatrixDto> matrixSetByRecord = matrixRepository.findMatrixSetByRecords(records);

                matrixList.add(
                        new ChallengeMapResponseDto.UserMapInfo(color, user.getLatitude(), user.getLongitude(), matrixSetByRecord, user.getPicturePath())
                );
            }
            //랭킹 계산
            //모든 회원의 칸 수 기록을 Tuple[닉네임, 이번주 누적 칸수] 내림차순으로 정리
            List<Tuple> matrixCount = exerciseRecordRepository.findMatrixCount(members, started, ended);
            rankings = rankService.calculateMatrixRank(matrixCount, members);
        }

        return new ChallengeMapResponseDto.Detail(matrixList, rankings);
    }

    /*챌린지 종류에 따른 랭킹 계산 메소드*/
    public List<UserResponseDto.Ranking> calculateChallengeRanking(Challenge challenge, List<User> members,
                                                                   LocalDate started, LocalDate ended, ChallengeType type) {

        List<ExerciseRecord> records = new ArrayList<>();
        List<UserResponseDto.Ranking> rankings = new ArrayList<>();

        if (type.equals(ChallengeType.WIDEN)) {
            for (User member : members) {
                //각 유저의 챌린지 기간동안의 기록
                records = exerciseRecordRepository.findRecord(member.getId(), started.atStartOfDay(), ended.atTime(LocalTime.MAX));

                //랭킹 리스트에 추가
                rankings.add(new UserResponseDto.Ranking(1, member.getNickname(),
                        (long) matrixRepository.findMatrixSetByRecords(records).size(), member.getPicturePath()));
            }
            //랭킹 정렬
            rankings = rankService.calculateAreaRank(rankings);
        } else if (challenge.getType().equals(ChallengeType.ACCUMULATE)) {
            //모든 회원의 칸 수 기록을 Tuple[닉네임, 이번주 누적 칸수] 내림차순으로 정리
            List<Tuple> matrixCount = exerciseRecordRepository.findMatrixCount(members, started.atStartOfDay(), ended.atTime(LocalTime.MAX));
            //랭킹 계산
            rankings = rankService.calculateMatrixRank(matrixCount, members);
        }

        return rankings;
    }

    /*챌린지 삭제*/
    public Boolean deleteChallenge(ChallengeRequestDto.CInfo request) {
        User user = userRepository.findByNickname(request.getNickname()).orElseThrow(
                () -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));

        Challenge challenge = challengeRepository.findByUuid(request.getUuid()).orElseThrow(
                () -> new ChallengeException(ExceptionCodeSet.CHALLENGE_NOT_FOUND, user.getNickname()));

        UserChallenge userChallenge = userChallengeRepository.findByUserAndChallenge(user, challenge).orElseThrow(
                () -> new ChallengeException(ExceptionCodeSet.USER_CHALLENGE_NOT_FOUND, user.getNickname()));

        //주최자만 삭제 가능.
        if (userChallenge.getStatus() != ChallengeStatus.MASTER) {
            return false;
        } else {
            List<UserChallenge> userChallenges = userChallengeRepository.findUCByChallenge(challenge);
            userChallengeRepository.deleteAll(userChallenges);
            challengeRepository.delete(challenge);
            return true;
        }
    }
}