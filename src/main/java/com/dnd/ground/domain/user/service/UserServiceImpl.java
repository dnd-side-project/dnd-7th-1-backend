package com.dnd.ground.domain.user.service;

import com.dnd.ground.domain.challenge.Challenge;
import com.dnd.ground.domain.challenge.repository.ChallengeRepository;
import com.dnd.ground.domain.challenge.repository.UserChallengeRepository;
import com.dnd.ground.domain.exerciseRecord.ExerciseRecord;
import com.dnd.ground.domain.exerciseRecord.Repository.ExerciseRecordRepository;
import com.dnd.ground.domain.friend.service.FriendService;
import com.dnd.ground.domain.matrix.dto.MatrixDto;
import com.dnd.ground.domain.matrix.matrixRepository.MatrixRepository;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.dto.HomeResponseDto;
import com.dnd.ground.domain.user.dto.UserResponseDto;
import com.dnd.ground.domain.user.repository.UserRepository;
import lombok.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @description 유저 서비스 클래스
 * @author  박세헌, 박찬호
 * @since   2022-08-01
 * @updated 1. 마이페이지 api 개발
 *          2022-08-16 - 박세헌
 */

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final ExerciseRecordRepository exerciseRecordRepository;
    private final ChallengeRepository challengeRepository;
    private final UserChallengeRepository userChallengeRepository;
    private final MatrixRepository matrixRepository;
    private final FriendService friendService;

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
            String challengeColor = challengeRepository.findChallengesWithFriend(user, friend).get(0).getColor(); // 챌린지 색
            List<ExerciseRecord> challengeRecordOfThisWeek = exerciseRecordRepository.findRecordOfThisWeek(friend.getId()); // 이번주 운동기록 조회
            List<MatrixDto> challengeMatrixSetDto = matrixRepository.findMatrixSetByRecords(challengeRecordOfThisWeek); // 운동 기록의 영역 조회

            challengeMatrices.add(new UserResponseDto.ChallengeMatrix(
                    friend.getNickname(), challengeNumber, challengeColor, friend.getLatitude(), friend.getLongitude(), challengeMatrixSetDto));
        }

        return HomeResponseDto.builder()
                .userMatrices(userMatrix)
                .friendMatrices(friendMatrices)
                .challengeMatrices(challengeMatrices)
                .challengesNumber(challengeRepository.findCountChallenge(user))
                .build();
    }

    /*회원 정보 조회*/
    public UserResponseDto.UInfo getUserInfo(String nickname) {
        User user = userRepository.findByNickname(nickname).orElseThrow();

        // 이번주 운동기록
        List<ExerciseRecord> recordOfThisWeek = exerciseRecordRepository.findRecordOfThisWeek(user.getId());

        // 이번주 영역의 수
        Long areaNumber = (long) matrixRepository.findMatrixSetByRecords(recordOfThisWeek).size();

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
                .areaNumber(areaNumber)
                .stepCount(stepCount)
                .distance(distance)
                .friendNumber(friendNumber)
                .allMatrixNumber(allMatrixNumber)
                .build();
    }
}