package com.dnd.ground.domain.user.service;

import com.dnd.ground.domain.challenge.Challenge;
import com.dnd.ground.domain.challenge.ChallengeColor;
import com.dnd.ground.domain.challenge.dto.ChallengeResponseDto;
import com.dnd.ground.domain.challenge.repository.ChallengeRepository;
import com.dnd.ground.domain.challenge.repository.UserChallengeRepository;
import com.dnd.ground.domain.challenge.service.ChallengeService;
import com.dnd.ground.domain.exerciseRecord.ExerciseRecord;
import com.dnd.ground.domain.exerciseRecord.Repository.ExerciseRecordRepository;
import com.dnd.ground.domain.exerciseRecord.dto.RecordRequestDto;
import com.dnd.ground.domain.exerciseRecord.dto.RecordResponseDto;
import com.dnd.ground.domain.friend.dto.FriendResponseDto;
import com.dnd.ground.domain.friend.repository.FriendRepository;
import com.dnd.ground.domain.friend.service.FriendService;
import com.dnd.ground.domain.matrix.dto.MatrixDto;
import com.dnd.ground.domain.matrix.matrixRepository.MatrixRepository;
import com.dnd.ground.domain.matrix.matrixService.MatrixService;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.dto.*;
import com.dnd.ground.domain.user.repository.UserRepository;
import com.dnd.ground.global.exception.CNotFoundException;
import com.dnd.ground.global.exception.CommonErrorCode;
import lombok.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @description 유저 서비스 클래스
 * @author  박세헌, 박찬호
 * @since   2022-08-01
 * @updated 2022-08-26 / 컨트롤러-서비스단 전달 형태 변경 - 박세헌
 */

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService, UserDetailsService{

    private final UserRepository userRepository;
    private final ChallengeService challengeService;
    private final ChallengeRepository challengeRepository;
    private final UserChallengeRepository userChallengeRepository;
    private final ExerciseRecordRepository exerciseRecordRepository;
    private final FriendService friendService;
    private final FriendRepository friendRepository;
    private final MatrixRepository matrixRepository;
    private final MatrixService matrixService;

    @Transactional
    public User save(JwtUserDto user){
        return userRepository.save(User
                .builder()
                .kakaoId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .mail(user.getMail())
                .created(LocalDateTime.now())
                .intro("")
                .latitude(null)
                .longitude(null)
                .isShowMine(true)
                .isShowFriend(true)
                .isPublicRecord(true)
                .build());
    }

    public HomeResponseDto showHome(String nickname){
        User user = userRepository.findByNickname(nickname).orElseThrow(
                () -> new CNotFoundException(CommonErrorCode.NOT_FOUND_USER));

        /*회원의 matrix 와 정보 (userMatrix)*/
        UserResponseDto.UserMatrix userMatrix = new UserResponseDto.UserMatrix(user);

        List<ExerciseRecord> userRecordOfThisWeek = exerciseRecordRepository.findRecordOfThisWeek(user.getId()); // 이번주 운동기록 조회
        List<MatrixDto> userMatrixSet = matrixRepository.findMatrixSetByRecords(userRecordOfThisWeek);  // 운동 기록의 영역 조회

        //회원 정보 저장
        userMatrix.setProperties(user.getNickname(), userMatrixSet.size(), userMatrixSet, user.getLatitude(), user.getLongitude());

        /*----------*/
        //진행 중인 챌린지 목록 조회 List<UserChallenge>
        List<Challenge> challenges = challengeRepository.findProgressChallenge(user);

        //챌린지를 함께하지 않는 친구 목록
        List<User> friendsNotChallenge = friendService.getFriends(user);

        //나랑 챌린지를 함께 하는 사람들(친구+친구X 둘 다)
        Set<User> friendsWithChallenge = new HashSet<>();

        for (Challenge challenge : challenges) {
            List<User> challengeUsers = userChallengeRepository.findChallengeUsers(challenge);
            //챌린지를 함께하고 있는 사람들 조회
            for (User cu : challengeUsers) {
                friendsWithChallenge.add(cu);
                friendsNotChallenge.remove(cu);
            }
        }
        friendsWithChallenge.remove(user);
        /*----------*/

        /*챌린지를 안하는 친구들의 matrix 와 정보 (friendMatrices)*/
        Map<String, List<MatrixDto>> friendHashMap= new HashMap<>();
        List<UserResponseDto.FriendMatrix> friendMatrices = new ArrayList<>();

        friendsNotChallenge.forEach(nf -> friendHashMap.put(nf.getNickname(),
                matrixRepository.findMatrixSetByRecords(exerciseRecordRepository.findRecordOfThisWeek(nf.getId()))));  // 이번주 운동기록 조회하여 영역 대입

        for (String friendNickname : friendHashMap.keySet()) {
            User friend = userRepository.findByNickname(friendNickname).orElseThrow(
                    () -> new CNotFoundException(CommonErrorCode.NOT_FOUND_USER));

            friendMatrices.add(new UserResponseDto.FriendMatrix(friendNickname, friend.getLatitude(), friend.getLongitude(),
                    friendHashMap.get(friendNickname)));
        }

        /*챌린지를 하는 사람들의 matrix 와 정보 (challengeMatrices)*/
        List<UserResponseDto.ChallengeMatrix> challengeMatrices = new ArrayList<>();

        for (User friend : friendsWithChallenge) {
            Integer challengeNumber = challengeRepository.findCountChallenge(user, friend); // 함께하는 챌린지 수

            //색깔 처리
            Challenge challengeWithFriend = challengeRepository.findChallengesWithFriend(user, friend).get(0);//함께하는 첫번째 챌린지 조회
            ChallengeColor challengeColor = userChallengeRepository.findChallengeColor(user, challengeWithFriend);//회원 기준 해당 챌린지 색깔

            List<ExerciseRecord> challengeRecordOfThisWeek = exerciseRecordRepository.findRecordOfThisWeek(friend.getId()); // 이번주 운동기록 조회
            List<MatrixDto> challengeMatrixSetDto = matrixRepository.findMatrixSetByRecords(challengeRecordOfThisWeek); // 운동 기록의 영역 조회

            challengeMatrices.add(
                    new UserResponseDto.ChallengeMatrix(
                            friend.getNickname(), challengeNumber, challengeColor,
                            friend.getLatitude(), friend.getLongitude(), challengeMatrixSetDto)
            );
        }

        return HomeResponseDto.builder()
                .userMatrices(userMatrix)
                .friendMatrices(friendMatrices)
                .challengeMatrices(challengeMatrices)
                .challengesNumber(challengeRepository.findCountChallenge(user))
                .isShowMine(user.getIsShowMine())
                .isShowFriend(user.getIsShowFriend())
                .isPublicRecord(user.getIsPublicRecord())
                .build();
    }

    /*회원 정보 조회(마이페이지)*/
    public UserResponseDto.Profile getUserInfo(String nickname) {
        User user = userRepository.findByNickname(nickname).orElseThrow(
                () -> new CNotFoundException(CommonErrorCode.NOT_FOUND_USER));

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

        return UserResponseDto.Profile.builder()
                .nickname(user.getNickname())
                .intro(user.getIntro())
                .matrixNumber(matrixNumber)
                .stepCount(stepCount)
                .distance(distance)
                .friendNumber(friendNumber)
                .allMatrixNumber(allMatrixNumber)
                .build();
    }

    /*회원 프로필 조회*/
    public FriendResponseDto.FriendProfile getUserProfile(String userNickname, String friendNickname) {
        User user = userRepository.findByNickname(userNickname).orElseThrow(
                () -> new CNotFoundException(CommonErrorCode.NOT_FOUND_USER));

        User friend = userRepository.findByNickname(friendNickname).orElseThrow(
                () -> new CNotFoundException(CommonErrorCode.NOT_FOUND_USER));

        //마지막 활동 시간
        LocalDateTime lasted = exerciseRecordRepository.findLastRecord(friend).orElseThrow(
                () -> new CNotFoundException(CommonErrorCode.NOT_FOUND_RECORD)
        );

        //친구 관계 확인
        Boolean isFriend = false;

        if (friendRepository.findFriendRelation(user, friend).isPresent()
                || friendRepository.findFriendRelation(friend, user).isPresent()) {
            isFriend = true;
        }

        //랭킹 추출 (이번 주 영역, 역대 누적 칸수, 랭킹)
        Integer rank = -1;
        Long allMatrixNumber = -1L;
        Long areas = -1L;

        RankResponseDto.Matrix matrixRanking = matrixService.matrixRanking(friendNickname);

        //역대 누적 칸수 및 랭킹 정보
        for (UserResponseDto.Ranking allRankInfo: matrixRanking.getMatrixRankings()) {
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
                .build();
    }

    /* 나의 활동 기록 조회 */
    public ActivityRecordResponseDto getActivityRecord(UserRequestDto.LookUp requestDto) {

        String nickname = requestDto.getNickname();
        LocalDateTime start = requestDto.getStart();
        LocalDateTime end = requestDto.getEnd();

        User user = userRepository.findByNickname(nickname).orElseThrow(
                () -> new CNotFoundException(CommonErrorCode.NOT_FOUND_USER));
        List<ExerciseRecord> record = exerciseRecordRepository.findRecord(user.getId(), start, end);  // start~end 사이 운동기록 조회
        List<RecordResponseDto.activityRecord> activityRecords = new ArrayList<>();

        Integer totalDistance = 0;  // 총 거리
        Integer totalExerciseTime = 0;  // 총 운동 시간
        Long totalMatrixNumber = 0L;  // 총 채운 칸의 수

        // 활동 내역 정보
        for (ExerciseRecord exerciseRecord : record) {

            // 운동 시작 시간 formatting
            String started = exerciseRecord.getStarted().format(DateTimeFormatter.ofPattern("MM월 dd일 E요일 HH:mm").withLocale(Locale.forLanguageTag("ko")));

            // 운동 시간 formatting
            Integer exerciseTime = exerciseRecord.getExerciseTime();
            String time = "";

            if (exerciseTime < 60){
                time = Integer.toString(exerciseTime) + "초";
            }
            else{
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
            totalDistance += exerciseRecord.getDistance();
            totalExerciseTime += exerciseRecord.getExerciseTime();
            totalMatrixNumber += (long) exerciseRecord.getMatrices().size();
        }

        // 총 운동 시간 formatting
        int totalMinute = totalExerciseTime / 60;
        int totalSecond = totalExerciseTime % 60;

        String totalTime = "";

        // 10초 미만이라면 앞에 0하나 붙여주기
        if (Integer.toString(totalSecond).length() == 1){
            totalTime = Integer.toString(totalMinute) + ":0" + Integer.toString(totalSecond);
            System.out.println(totalTime);
        }
        else{
            totalTime = Integer.toString(totalMinute) + ":" + Integer.toString(totalSecond);
            System.out.println(totalTime);
        }

        return ActivityRecordResponseDto
                .builder()
                .activityRecords(activityRecords)
                .totalMatrixNumber(totalMatrixNumber)
                .totalDistance(totalDistance)
                .totalExerciseTime(totalTime)
                .build();
    }

    /* 나의 운동기록에 대한 정보 조회 */
    public RecordResponseDto.EInfo getExerciseInfo(Long exerciseId){
        ExerciseRecord exerciseRecord = exerciseRecordRepository.findById(exerciseId).orElseThrow(
                () -> new CNotFoundException(CommonErrorCode.NOT_FOUND_RECORD));
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
        }
        else {
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
    public UserResponseDto.DetailMap getDetailMap(Long recordId){
        // 운동 기록 찾기
        ExerciseRecord exerciseRecord = exerciseRecordRepository.findById(recordId).orElseThrow(
                () -> new CNotFoundException(CommonErrorCode.NOT_FOUND_RECORD));
        // 유저 찾기
        User user = userRepository.findByExerciseRecord(exerciseRecord).orElseThrow(
                () -> new CNotFoundException(CommonErrorCode.NOT_FOUND_USER));
        // 운동기록의 칸 찾기
        List<MatrixDto> matrices = matrixRepository.findMatrixSetByRecord(exerciseRecord);

        return new UserResponseDto.DetailMap(user.getLatitude(),
                user.getLongitude(), matrices);
    }

    /*필터 변경: 나의 기록 보기*/
    @Transactional
    public Boolean changeFilterMine(String nickname) {
        return userRepository.findByNickname(nickname)
                .orElseThrow(() -> new CNotFoundException(CommonErrorCode.NOT_FOUND_USER))
                .changeFilterMine();
    }

    /*필터 변경: 친구 보기*/
    @Transactional
    public Boolean changeFilterFriend(String nickname) {
        return userRepository.findByNickname(nickname)
                .orElseThrow(() -> new CNotFoundException(CommonErrorCode.NOT_FOUND_USER))
                .changeFilterFriend();
    }

    /*필터 변경: 친구들에게 보이기*/
    @Transactional
    public Boolean changeFilterRecord(String nickname) {
        return userRepository.findByNickname(nickname)
                .orElseThrow(() -> new CNotFoundException(CommonErrorCode.NOT_FOUND_USER))
                .changeFilterRecord();
    }

    /* 운동 기록의 상세 메시지 수정 */
    @Transactional
    public ResponseEntity<Boolean> editRecordMessage(RecordRequestDto.Message requestDto){
        Long recordId = requestDto.getRecordId();
        String message = requestDto.getMessage();

        ExerciseRecord exerciseRecord = exerciseRecordRepository.findById(recordId).orElseThrow(
                () -> new CNotFoundException(CommonErrorCode.NOT_FOUND_RECORD));
        exerciseRecord.editMessage(message);
        return new ResponseEntity(true, HttpStatus.OK);
    }

    /* 회원 프로필 수정 */
    @Transactional
    public ResponseEntity<Boolean> editUserProfile(UserRequestDto.Profile requestDto){

        String originalNick = requestDto.getOriginalNick();
        String editNick = requestDto.getEditNick();
        String intro = requestDto.getIntro();

        User user = userRepository.findByNickname(originalNick).orElseThrow(
                () -> new CNotFoundException(CommonErrorCode.NOT_FOUND_USER));
        user.updateProfile(editNick, intro);
        return new ResponseEntity(true, HttpStatus.OK);
    }

    @Override
    public UserDetails loadUserByUsername(String nickname) {
        User user = userRepository.findByNickname(nickname).orElseThrow(); // 예외처리
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return org.springframework.security.core.userdetails.User
                .builder()
                .username(nickname)
                .password(passwordEncoder.encode(user.getKakaoId()+user.getNickname()))
                .authorities("BASIC")
                .build();
        }
}