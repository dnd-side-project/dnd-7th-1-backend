package com.dnd.ground.domain.user.service;

import com.dnd.ground.domain.challenge.Challenge;
import com.dnd.ground.domain.challenge.repository.ChallengeRepository;
import com.dnd.ground.domain.challenge.repository.UserChallengeRepository;
import com.dnd.ground.domain.exerciseRecord.ExerciseRecord;
import com.dnd.ground.domain.exerciseRecord.Repository.ExerciseRecordRepository;
import com.dnd.ground.domain.exerciseRecord.service.ExerciseRecordService;
import com.dnd.ground.domain.friend.service.FriendService;
import com.dnd.ground.domain.matrix.dto.MatrixDto;
import com.dnd.ground.domain.matrix.matrixRepository.MatrixRepository;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.dto.HomeResponseDto;
import com.dnd.ground.domain.user.dto.RankResponseDto;
import com.dnd.ground.domain.user.dto.UserResponseDto;
import com.dnd.ground.domain.user.repository.UserRepository;
import lombok.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Tuple;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

/**
 * @description 유저 서비스 클래스
 * @author  박세헌, 박찬호
 * @since   2022-08-01

 * @updated 1. 랭킹 관련 메소드 이동(UserService -> MatrixService)
 *          - 2022.08.11 박찬호
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
        User user = userRepository.findByNickName(nickname).orElseThrow();  // 예외 처리

        /*유저의 matrix 와 정보 (userMatrix)*/
        UserResponseDto.UserMatrix userMatrix = new UserResponseDto.UserMatrix(user);

        List<ExerciseRecord> userRecordOfThisWeek = exerciseRecordRepository.findRecordOfThisWeek(user.getId()); // 이번주 운동기록 조회
        List<MatrixDto> userMatrixSet = matrixRepository.findMatrixSetByRecords(userRecordOfThisWeek);  // 운동 기록의 영역 조회

        userMatrix.setProperties(nickname, userMatrixSet.size(), userMatrixSet, user.getLatitude(), user.getLongitude());

        /*----------*/
        //진행 중인 챌린지 목록 조회 List<UserChallenge>
        List<Challenge> challenges = challengeRepository.findChallenge(user);

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

        friendsNotChallenge.forEach(nf -> friendHashMap.put(nf.getNickName(),
                matrixRepository.findMatrixSetByRecords(exerciseRecordRepository.findRecordOfThisWeek(nf.getId()))));  // 이번주 운동기록 조회하여 영역 대입

        List<UserResponseDto.FriendMatrix> friendMatrices = new ArrayList<>();
        for (String friendNickname : friendHashMap.keySet()) {
            User friend = userRepository.findByNickName(friendNickname).orElseThrow(); //예외 처리 예정
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
                    friend.getNickName(), challengeNumber, challengeColor, friend.getLatitude(), friend.getLongitude(), challengeMatrixSetDto));
        }

        return HomeResponseDto.builder()
                .userMatrices(userMatrix)
                .friendMatrices(friendMatrices)
                .challengeMatrices(challengeMatrices)
                .challengesNumber(challengeRepository.findCountChallenge(user))
                .build();
    }
}