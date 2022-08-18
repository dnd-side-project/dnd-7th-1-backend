package com.dnd.ground.domain.user.service;

import com.dnd.ground.domain.challenge.Challenge;
import com.dnd.ground.domain.challenge.ChallengeColor;
import com.dnd.ground.domain.challenge.dto.ChallengeResponseDto;
import com.dnd.ground.domain.challenge.repository.ChallengeRepository;
import com.dnd.ground.domain.challenge.repository.UserChallengeRepository;
import com.dnd.ground.domain.challenge.service.ChallengeService;
import com.dnd.ground.domain.exerciseRecord.ExerciseRecord;
import com.dnd.ground.domain.exerciseRecord.Repository.ExerciseRecordRepository;
import com.dnd.ground.domain.exerciseRecord.dto.RecordResponseDto;
import com.dnd.ground.domain.friend.repository.FriendRepository;
import com.dnd.ground.domain.friend.service.FriendService;
import com.dnd.ground.domain.matrix.dto.MatrixDto;
import com.dnd.ground.domain.matrix.matrixRepository.MatrixRepository;
import com.dnd.ground.domain.matrix.matrixService.MatrixService;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.dto.ActivityRecordResponseDto;
import com.dnd.ground.domain.user.dto.HomeResponseDto;
import com.dnd.ground.domain.user.dto.RankResponseDto;
import com.dnd.ground.domain.user.dto.UserResponseDto;
import com.dnd.ground.domain.user.repository.UserRepository;
import lombok.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @description 유저 서비스 클래스
 * @author  박세헌, 박찬호
 * @since   2022-08-01
 * @updated 1. matrixRanking함수 파라미터 변경
 *          2. 활동 기록의 운동 시간 1분 미만 이면 초로 변환
 *          - 2022.08.18 박세헌
 */

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService{

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
    public User save(User user){
        return userRepository.save(user);
    }

    public HomeResponseDto showHome(String nickname){
        User user = userRepository.findByNickname(nickname).orElseThrow();  // 예외 처리

        /*유저의 matrix 와 정보 (userMatrix)*/
        UserResponseDto.UserMatrix userMatrix = new UserResponseDto.UserMatrix(user);

        List<ExerciseRecord> userRecordOfThisWeek = exerciseRecordRepository.findRecordOfThisWeek(user.getId()); // 이번주 운동기록 조회
        List<MatrixDto> userMatrixSet = matrixRepository.findMatrixSetByRecords(userRecordOfThisWeek);  // 운동 기록의 영역 조회

        userMatrix.setProperties(nickname, userMatrixSet.size(), userMatrixSet, user.getLatitude(), user.getLongitude());

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

        friendsNotChallenge.forEach(nf -> friendHashMap.put(nf.getNickname(),
                matrixRepository.findMatrixSetByRecords(exerciseRecordRepository.findRecordOfThisWeek(nf.getId()))));  // 이번주 운동기록 조회하여 영역 대입

        List<UserResponseDto.FriendMatrix> friendMatrices = new ArrayList<>();
        for (String friendNickname : friendHashMap.keySet()) {
            User friend = userRepository.findByNickname(friendNickname).orElseThrow(); //예외 처리 예정
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
                .build();
    }

    /*회원 정보 조회(마이페이지)*/
    public UserResponseDto.UInfo getUserInfo(String nickname) {
        User user = userRepository.findByNickname(nickname).orElseThrow();

        // 이번주 운동기록
        List<ExerciseRecord> recordOfThisWeek = exerciseRecordRepository.findRecordOfThisWeek(user.getId());

        // 이번주 채운 칸의 수
        Long matrixNumber = (long) matrixRepository.findMatrixByRecords(recordOfThisWeek).size();

        // 이번주 걸음수
        Integer stepCount = exerciseRecordRepository.findUserStepCount(user, recordOfThisWeek);

        // 이번주 거리합
        Integer distance = exerciseRecordRepository.findUserDistance(user, recordOfThisWeek);

        // 친구 수
        Integer friendNumber = friendService.getFriends(user).size();

        // 역대 누적 운동기록(가입날짜 ~ 지금)
        List<ExerciseRecord> record = exerciseRecordRepository.findRecord(user.getId(), user.getCreated(), LocalDateTime.now());
        // 역대 누적 칸수
        Long allMatrixNumber = (long) matrixRepository.findMatrixByRecords(record).size();

        return UserResponseDto.UInfo.builder()
                .nickname(nickname)
                .intro(user.getIntro())
                .matrixNumber(matrixNumber)
                .stepCount(stepCount)
                .distance(distance)
                .friendNumber(friendNumber)
                .allMatrixNumber(allMatrixNumber)
                .build();
    }

    /*회원 프로필 조회*/
    public UserResponseDto.Profile getUserProfile(String userNickname, String friendNickname) {
        User user = userRepository.findByNickname(userNickname).orElseThrow(); //예외 처리 예정
        User friend = userRepository.findByNickname(friendNickname).orElseThrow(); //예외 처리 예정

        //마지막 활동 시간
        LocalDateTime lastRecord = exerciseRecordRepository.findLastRecord(friend).orElseThrow(); //예외 처리 예정
        String lasted = lastRecord.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); //친구의 마지막 활동 시간

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
        areas = (long)matrixRepository.findMatrixSetByRecords(
                exerciseRecordRepository.findRecordOfThisWeek(friend.getId())).size();

        //함께 진행하는 챌린지 정보
        List<ChallengeResponseDto.Progress> challenges = challengeService.findProgressChallenge(userNickname, friendNickname);

        return UserResponseDto.Profile.builder()
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
    public ActivityRecordResponseDto getActivityRecord(String nickname, LocalDateTime start, LocalDateTime end) {
        User user = userRepository.findByNickname(nickname).orElseThrow();  // 예외처리 예정
        List<ExerciseRecord> record = exerciseRecordRepository.findRecord(user.getId(), start, end);  // start~end 사이 운동기록 조회
        List<RecordResponseDto.activityRecord> activityRecords = new ArrayList<>();

        Integer totalDistance = 0;  // 총 거리
        Integer totalExerciseTime = 0;  // 총 운동 시간
        Long totalMatrixNumber = 0L;  // 총 채운 칸의 수

        // 활동 내역 정보
        for (ExerciseRecord exerciseRecord : record) {

            // 운동 시작 시간 formatting
            String started = exerciseRecord.getStarted().format(DateTimeFormatter.ofPattern("MM월 dd일 E요일 HH:mm"));

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
                    .exerciseId(exerciseRecord.getId())
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

        String totalTime = Integer.toString(totalMinute) + ":" + Integer.toString(totalSecond);

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
        ExerciseRecord exerciseRecord = exerciseRecordRepository.findById(exerciseId).orElseThrow();  // 예외 처리

        // 운동 시작, 끝 시간 formatting
        String date = exerciseRecord.getStarted().format(DateTimeFormatter.ofPattern("MM월 dd일 E요일"));
        String started = exerciseRecord.getStarted().format(DateTimeFormatter.ofPattern("HH:mm"));
        String ended = exerciseRecord.getEnded().format(DateTimeFormatter.ofPattern("HH:mm"));

        // 운동 시간 formatting
        Integer exerciseTime = exerciseRecord.getExerciseTime();
        int minute = exerciseTime / 60;
        int second = exerciseTime % 60;
        String time = Integer.toString(minute) + ":" + Integer.toString(second);

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
                .build();
    }

    /* 상세 지도 보기 */
    public UserResponseDto.DetailMap getDetailMap(Long recordId){
        // 운동 기록 찾기
        ExerciseRecord exerciseRecord = exerciseRecordRepository.findById(recordId).orElseThrow();  // 예외 처리
        // 유저 찾기
        User user = userRepository.findByExerciseRecord(exerciseRecord).orElseThrow();  // 예외 처리
        // 운동기록의 칸 찾기
        List<MatrixDto> matrices = matrixRepository.findMatrixSetByRecord(exerciseRecord);

        return new UserResponseDto.DetailMap(user.getLatitude(),
                user.getLongitude(), matrices);
    }

}