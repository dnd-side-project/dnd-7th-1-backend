package com.dnd.ground.domain.user.service;

import com.dnd.ground.domain.challenge.Challenge;
import com.dnd.ground.domain.challenge.ChallengeColor;
import com.dnd.ground.domain.challenge.dto.ChallengeColorDto;
import com.dnd.ground.domain.challenge.dto.ChallengeResponseDto;
import com.dnd.ground.domain.challenge.repository.ChallengeRepository;
import com.dnd.ground.domain.challenge.repository.UserChallengeRepository;
import com.dnd.ground.domain.challenge.service.ChallengeService;
import com.dnd.ground.domain.exerciseRecord.ExerciseRecord;
import com.dnd.ground.domain.exerciseRecord.Repository.ExerciseRecordRepository;
import com.dnd.ground.domain.exerciseRecord.dto.RecordRequestDto;
import com.dnd.ground.domain.exerciseRecord.dto.RecordResponseDto;
import com.dnd.ground.domain.friend.FriendStatus;
import com.dnd.ground.domain.friend.dto.FriendCondition;
import com.dnd.ground.domain.friend.dto.FriendResponseDto;
import com.dnd.ground.domain.friend.repository.FriendRepository;
import com.dnd.ground.domain.friend.service.FriendService;
import com.dnd.ground.domain.matrix.dto.MatrixDto;
import com.dnd.ground.domain.matrix.matrixRepository.MatrixRepository;
import com.dnd.ground.domain.matrix.matrixService.MatrixService;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.dto.*;
import com.dnd.ground.domain.user.repository.UserRepository;
import com.dnd.ground.global.auth.UserClaim;
import com.dnd.ground.global.auth.service.AuthService;
import com.dnd.ground.global.exception.*;
import com.dnd.ground.global.util.AmazonS3Service;
import com.dnd.ground.domain.matrix.dto.Location;
import lombok.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

/**
 * @description 유저 서비스 클래스
 * @author 박세헌, 박찬호
 * @since 2022-08-01
 * @updated 1.메인 화면 조회 시, 범위 내 영역 조회하도록 수정 및 DB 쿼리 요청 수 최소화
 *          - 2023-02-15 박찬호
 */

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ChallengeService challengeService;
    private final ChallengeRepository challengeRepository;
    private final ExerciseRecordRepository exerciseRecordRepository;
    private final FriendService friendService;
    private final FriendRepository friendRepository;
    private final MatrixRepository matrixRepository;
    private final MatrixService matrixService;
    private final AmazonS3Service amazonS3Service;
    private final AuthService authService;

    @Value("${picture.path}")
    private String DEFAULT_PATH;

    @Value("${picture.name}")
    private String DEFAULT_NAME;

    public HomeResponseDto showHome(UserRequestDto.Home request) {
        User user = userRepository.findByNickname(request.getNickname()).orElseThrow(
                () -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));

        Location location = request.getLocation();

        /*회원 영역 조회*/
        List<Location> userMatricesThisWeek = matrixRepository.findMatrixPoint(user, location);

        UserResponseDto.UserMatrix userMatrix  = UserResponseDto.UserMatrix.builder()
                .nickname(user.getNickname())
                .matricesNumber((long) userMatricesThisWeek.size())
                .latitude(user.getLatitude())
                .longitude(user.getLongitude())
                .matrices(userMatricesThisWeek)
                .picturePath(user.getPicturePath())
                .build();

        /*----------*/
        //챌린지 멤버 조회
        List<User> challengeMembers = challengeRepository.findUCInProgress(user);

        //친구 목록 조회
        FriendCondition friendCond = FriendCondition.builder()
                .user(user)
                .status(FriendStatus.Accept)
                .build();
        List<User> friends = friendRepository.findFriends(friendCond);
        friends.removeAll(challengeMembers); // 챌린지를 함께하는 친구 제외

        //친구, 챌린지 멤버의 영역 한 번에 조회
        Set<User> friendsMembers = new HashSet<>();
        friendsMembers.addAll(challengeMembers);
        friendsMembers.addAll(friends);
        Map<User, List<Location>> usersMatrix = matrixRepository.findUsersMatrix(friendsMembers, location);
        /*----------*/

        /*친구 Response 생성*/
        List<UserResponseDto.FriendMatrix> friendMatrices = new ArrayList<>();

        for (User friend : friends) {
            friendMatrices.add(
                    UserResponseDto.FriendMatrix.builder()
                            .nickname(friend.getNickname())
                            .latitude(friend.getLatitude())
                            .longitude(friend.getLongitude())
                            .picturePath(friend.getPicturePath())
                            .matrices(usersMatrix.get(friend))
                            .build()
            );
        }
        /*----------*/

        /*챌린지 멤버 Response 생성*/
        List<UserResponseDto.ChallengeMatrix> challengeMatrices = new ArrayList<>();

        //함께하는 챌린지 개수 조회
        Map<User, Long> challengeCount = challengeRepository.findProgressChallengeCount(user);

        //챌린지 진행 정보 조회
        Map<User, Challenge> challengeInfo = challengeRepository.findProgressChallengesInfo(user);

        //챌린지 색깔 조회
        List<ChallengeColorDto> challengesColor = challengeRepository.findProgressChallengesColor(user);

        for (User member : challengeMembers) {
            //챌린지 색깔 탐색
            ChallengeColor color = null;
            for (ChallengeColorDto challengeColorDto : challengesColor) {
                color = challengeColorDto.findColor(challengeInfo.get(member));
                if (color != null) break;
            }

            challengeMatrices.add(
                    UserResponseDto.ChallengeMatrix.builder()
                            .nickname(member.getNickname())
                            .latitude(member.getLatitude())
                            .longitude(member.getLongitude())
                            .picturePath(member.getPicturePath())
                            .challengeNumber(challengeCount.get(member))
                            .challengeColor(color)
                            .matrices(usersMatrix.get(member))
                            .build()
            );
        }

        return HomeResponseDto.builder()
                .userMatrices(userMatrix)
                .friendMatrices(friendMatrices)
                .challengeMatrices(challengeMatrices)
                .challengesNumber(challengesColor.size())
                .isShowMine(user.getIsShowMine())
                .isShowFriend(user.getIsShowFriend())
                .isPublicRecord(user.getIsPublicRecord())
                .build();
    }

    /*회원 정보 조회(마이페이지)*/
    public UserResponseDto.MyPage getUserInfo(String nickname) {
        User user = userRepository.findByNickname(nickname).orElseThrow(
                () -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));

        // 이번주 운동기록
        List<ExerciseRecord> recordOfThisWeek = exerciseRecordRepository.findRecordOfThisWeek(user.getId());

        // 이번주 채운 칸의 수
        Long matrixNumber = (long) matrixRepository.findMatrixByRecords(recordOfThisWeek).size();

        // 이번주 걸음수
        Integer stepCount = exerciseRecordRepository.findUserStepCount(user, recordOfThisWeek).orElse(0);

        // 이번주 거리합
        Integer distance = exerciseRecordRepository.findUserDistance(user, recordOfThisWeek).orElse(0);

        // 친구 수
        Integer friendNumber = friendService.getFriends(user).size();

        // 역대 누적 운동기록(가입날짜 ~ 지금)
        List<ExerciseRecord> record = exerciseRecordRepository.findRecord(user.getId(), user.getCreated(), LocalDateTime.now());
        // 역대 누적 칸수
        Long allMatrixNumber = (long) matrixRepository.findMatrixByRecords(record).size();

        return UserResponseDto.MyPage.builder()
                .nickname(user.getNickname())
                .intro(user.getIntro())
                .matrixNumber(matrixNumber)
                .stepCount(stepCount)
                .distance(distance)
                .friendNumber(friendNumber)
                .allMatrixNumber(allMatrixNumber)
                .picturePath(user.getPicturePath())
                .build();
    }

    /*회원 프로필 조회*/
    public FriendResponseDto.FriendProfile getUserProfile(String userNickname, String friendNickname) {
        User user = userRepository.findByNickname(userNickname).orElseThrow(
                () -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));

        User friend = userRepository.findByNickname(friendNickname).orElseThrow(
                () -> new FriendException(ExceptionCodeSet.FRIEND_NOT_FOUND));

        //마지막 활동 시간
        Optional<LocalDateTime> lastedOpt = exerciseRecordRepository.findLastRecord(friend);
        LocalDateTime lasted = null;
        if (lastedOpt.isPresent())
            lasted = lastedOpt.get();


        //친구 관계 확인
        FriendStatus isFriend = friendService.getFriendStatus(user, friend);

        //랭킹 추출 (이번 주 영역, 역대 누적 칸수, 랭킹)
        Integer rank = -1;
        Long allMatrixNumber = -1L;
        Long areas = -1L;

        RankResponseDto.Matrix matrixRanking = matrixService.matrixRanking(friendNickname);

        //역대 누적 칸수 및 랭킹 정보
        for (UserResponseDto.Ranking allRankInfo : matrixRanking.getMatrixRankings()) {
            if (allRankInfo.getNickname().equals(friendNickname)) {
                rank = allRankInfo.getRank();
                allMatrixNumber = allRankInfo.getScore();
            }
        }

        //이번주 영역 정보
        areas = (long) matrixRepository.findMatrixSetByRecords(
                exerciseRecordRepository.findRecordOfThisWeek(friend.getId())).size();

        //함께 진행하는 챌린지 정보
        List<ChallengeResponseDto.Progress> challenges = challengeService.findProgressChallenge(userNickname, friendNickname);

        return FriendResponseDto.FriendProfile.builder()
                .nickname(friendNickname)
                .lasted(lasted)
                .intro(friend.getIntro())
                .isFriend(isFriend)
                .areas(areas)
                .allMatrixNumber(allMatrixNumber)
                .rank(rank)
                .challenges(challenges)
                .picturePath(friend.getPicturePath())
                .build();
    }

    /* 나의 활동 기록 조회 */
    public UserResponseDto.ActivityRecordResponseDto getActivityRecord(UserRequestDto.LookUp requestDto) {

        String nickname = requestDto.getNickname();
        LocalDateTime start = requestDto.getStart();
        LocalDateTime end = requestDto.getEnd();

        User user = userRepository.findByNickname(nickname).orElseThrow(
                () -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));
        List<ExerciseRecord> record = exerciseRecordRepository.findRecord(user.getId(), start, end);  // start~end 사이 운동기록 조회
        List<RecordResponseDto.activityRecord> activityRecords = new ArrayList<>();


        // 활동 내역 정보
        for (ExerciseRecord exerciseRecord : record) {

            // 운동 시작 시간 formatting
            String started = exerciseRecord.getStarted().format(DateTimeFormatter.ofPattern("MM월 dd일 E요일 HH:mm").withLocale(Locale.forLanguageTag("ko")));

            // 운동 시간 formatting
            Integer exerciseTime = exerciseRecord.getExerciseTime();
            String time = "";

            if (exerciseTime < 60) {
                time = Integer.toString(exerciseTime) + "초";
            } else {
                time = Integer.toString(exerciseTime / 60) + "분";
            }

            activityRecords.add(RecordResponseDto.activityRecord
                    .builder()
                    .recordId(exerciseRecord.getId())
                    .matrixNumber((long) exerciseRecord.getMatrices().size())
                    .stepCount(exerciseRecord.getStepCount())
                    .distance(exerciseRecord.getDistance())
                    .exerciseTime(time)
                    .started(started)
                    .build());
        }

        return UserResponseDto.ActivityRecordResponseDto
                .builder()
                .activityRecords(activityRecords)
                .build();
    }

    /* 나의 운동기록에 대한 정보 조회 */
    public RecordResponseDto.EInfo getExerciseInfo(Long exerciseId) {
        ExerciseRecord exerciseRecord = exerciseRecordRepository.findById(exerciseId).orElseThrow(
                () -> new ExerciseRecordException(ExceptionCodeSet.RECORD_NOT_FOUND));
        // 운동 시작, 끝 시간 formatting
        String date = exerciseRecord.getStarted().format(DateTimeFormatter.ofPattern("MM월 dd일 E요일").withLocale(Locale.forLanguageTag("ko")));
        String started = exerciseRecord.getStarted().format(DateTimeFormatter.ofPattern("HH:mm"));
        String ended = exerciseRecord.getEnded().format(DateTimeFormatter.ofPattern("HH:mm"));

        // 운동 시간 formatting
        Integer exerciseTime = exerciseRecord.getExerciseTime();
        int minute = exerciseTime / 60;
        int second = exerciseTime % 60;
        String time = "";

        // 10초 미만이라면 앞에 0하나 붙여주기
        if (Integer.toString(second).length() == 1) {
            time = minute + ":0" + second;
        } else {
            time = minute + ":" + second;
        }

        // 해당 운동 기록이 참여한 챌린지들 조회
        List<ChallengeResponseDto.CInfoRes> challenges = challengeService.findChallengeByRecord(exerciseRecord);

        return RecordResponseDto.EInfo
                .builder()
                .recordId(exerciseRecord.getId())
                .date(date)
                .started(started)
                .ended(ended)
                .matrixNumber((long) exerciseRecord.getMatrices().size())
                .distance(exerciseRecord.getDistance())
                .exerciseTime(time)
                .stepCount(exerciseRecord.getStepCount())
                .message(exerciseRecord.getMessage())
                .matrices(matrixRepository.findMatrixSetByRecord(exerciseRecord))
                .challenges(challenges)
                .build();
    }

    /* 상세 지도 보기 */
    public UserResponseDto.DetailMap getDetailMap(Long recordId) {
        // 운동 기록 찾기
        ExerciseRecord exerciseRecord = exerciseRecordRepository.findById(recordId).orElseThrow(
                () -> new ExerciseRecordException(ExceptionCodeSet.RECORD_NOT_FOUND));
        // 유저 찾기
        User user = userRepository.findByExerciseRecord(exerciseRecord).orElseThrow(
                () -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));
        // 운동기록의 칸 찾기
        List<MatrixDto> matrices = matrixRepository.findMatrixSetByRecord(exerciseRecord);

        return new UserResponseDto.DetailMap(user.getLatitude(),
                user.getLongitude(), matrices, user.getPicturePath());
    }

    /*필터 변경: 나의 기록 보기*/
    @Transactional
    public Boolean changeFilterMine(String nickname) {
        return userRepository.findByNickname(nickname)
                .orElseThrow(() -> new UserException(ExceptionCodeSet.USER_NOT_FOUND))
                .changeFilterMine();
    }

    /*필터 변경: 친구 보기*/
    @Transactional
    public Boolean changeFilterFriend(String nickname) {
        return userRepository.findByNickname(nickname)
                .orElseThrow(() -> new UserException(ExceptionCodeSet.USER_NOT_FOUND))
                .changeFilterFriend();
    }

    /*필터 변경: 친구들에게 보이기*/
    @Transactional
    public Boolean changeFilterRecord(String nickname) {
        return userRepository.findByNickname(nickname)
                .orElseThrow(() -> new UserException(ExceptionCodeSet.USER_NOT_FOUND))
                .changeFilterRecord();
    }

    /* 운동 기록의 상세 메시지 수정 */
    @Transactional
    public ResponseEntity<Boolean> editRecordMessage(RecordRequestDto.Message requestDto) {
        Long recordId = requestDto.getRecordId();
        String message = requestDto.getMessage();

        ExerciseRecord exerciseRecord = exerciseRecordRepository.findById(recordId).orElseThrow(
                () -> new ExerciseRecordException(ExceptionCodeSet.RECORD_NOT_FOUND));
        exerciseRecord.editMessage(message);
        return new ResponseEntity(true, HttpStatus.OK);
    }

    /* 회원 프로필 수정 */
    @Transactional
    public ResponseEntity<UserResponseDto.UInfo> editUserProfile(MultipartFile file, UserRequestDto.Profile requestDto) {
        String originalNick = requestDto.getOriginNickname();
        String editNick = requestDto.getEditNickname();

        //변경될 닉네임 중복 검사
        if (!editNick.equals(originalNick) && !authService.validateNickname(editNick))
            throw new UserException(ExceptionCodeSet.DUPLICATE_NICKNAME);

        User user = userRepository.findByNickname(originalNick).orElseThrow(
                () -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));

        String intro = requestDto.getIntro();
        String pictureName = user.getPictureName();
        String picturePath = user.getPicturePath();

        // 기본 이미지로 변경
        if (requestDto.getIsBasic()) {
            pictureName = DEFAULT_NAME;
            picturePath = DEFAULT_PATH;
            if (!user.getPictureName().equals(pictureName)) amazonS3Service.deleteFile(user.getPictureName());
        } else {
            // 기본 이미지가 아닌 유저의 사진으로 변경 (프로필 사진 이름: 닉네임+카카오ID (Ex. NickA18345)
            if (!file.isEmpty()) {
                amazonS3Service.deleteFile(user.getPictureName());
                Map<String, String> fileInfo = amazonS3Service.uploadToS3(file, "user/profile", user.getEmail() + UserClaim.changeCreatedToLong(user.getCreated()));
                pictureName = fileInfo.get("fileName");
                picturePath = fileInfo.get("filePath");
            }
        }
        user.updateProfile(editNick, intro, pictureName, picturePath);

        return ResponseEntity.ok(new UserResponseDto.UInfo(editNick, picturePath));
    }

    public UserResponseDto.dayEventList getDayEventList(UserRequestDto.DayEventList requestDto) {

        LocalDate startDay = requestDto.getYearMonth().with(firstDayOfMonth());
        LocalDate endDay = requestDto.getYearMonth().with(lastDayOfMonth());

        LocalTime startTime = LocalTime.of(0, 0, 0);
        LocalTime endTime = LocalTime.of(23, 59, 59);

        LocalDateTime start = LocalDateTime.of(startDay, startTime);
        LocalDateTime end = LocalDateTime.of(endDay, endTime);

        User user = userRepository.findByNickname(requestDto.getNickname())
                .orElseThrow(() -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));

        return new UserResponseDto.dayEventList(
                exerciseRecordRepository.findDayEventList(user, start, end)
                        .stream()
                        .map(LocalDate::parse)
                        .collect(Collectors.toList()));
    }

    /*마이페이지 프로필 조회*/
    public UserResponseDto.Profile getUserProfile(String nickname) {
        User user = userRepository.findByNickname(nickname).orElseThrow(
                () -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));

        return UserResponseDto.Profile.builder()
                .nickname(user.getNickname())
                .intro(user.getIntro())
                .mail(user.getEmail())
                .picturePath(user.getPicturePath())
                .build();
    }
}