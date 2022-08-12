package com.dnd.ground.domain.user.service;

import com.dnd.ground.domain.challenge.Challenge;
import com.dnd.ground.domain.challenge.repository.ChallengeRepository;
import com.dnd.ground.domain.challenge.repository.UserChallengeRepository;
import com.dnd.ground.domain.exerciseRecord.ExerciseRecord;
import com.dnd.ground.domain.exerciseRecord.Repository.ExerciseRecordRepository;
import com.dnd.ground.domain.friend.service.FriendService;
import com.dnd.ground.domain.matrix.Matrix;
import com.dnd.ground.domain.matrix.dto.MatrixSetDto;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.dto.HomeResponseDto;
import com.dnd.ground.domain.user.dto.UserResponseDto;
import com.dnd.ground.domain.user.repository.UserRepository;
import lombok.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @description 유저 서비스 클래스
 * @author  박세헌, 박찬호
 * @since   2022-08-01
 * @updated 1. 회원 정보 조회 메소드 추가
 *          - 2022.08.11 박찬호
 */

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final ExerciseRecordRepository exerciseRecordRepository;
    private final ChallengeRepository challengeRepository;
    private final UserChallengeRepository userChallengeRepository;
    private final FriendService friendService;

    @Transactional
    public User save(User user){
        return userRepository.save(user);
    }

    public HomeResponseDto showHome(String nickname){
        User user = userRepository.findByNickname(nickname).orElseThrow();  // 예외 처리

        /*유저의 matrix 와 정보 (userMatrix)*/
        Set<MatrixSetDto> userShowMatrices = new HashSet<>();
        UserResponseDto.UserMatrix userMatrix = new UserResponseDto.UserMatrix(user);

        List<ExerciseRecord> userRecordOfThisWeek = exerciseRecordRepository.findRecordOfThisWeek(user.getId());

        if (!userRecordOfThisWeek.isEmpty()) {
            List<List<Matrix>> userMatrices = userRecordOfThisWeek.stream()
                    .map(ExerciseRecord::getMatrices)
                    .collect(Collectors.toList());

            userMatrices.forEach(ms -> ms.forEach(m ->
                    userShowMatrices.add(
                            MatrixSetDto.builder()
                            .latitude(m.getLatitude())
                            .longitude(m.getLongitude())
                            .build())
                    )
            );

            userMatrix.setProperties(nickname, userShowMatrices.size(), userShowMatrices, user.getLatitude(), user.getLongitude());
        }

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
        Map<String, Set<MatrixSetDto>> friendHashMap= new HashMap<>();

        friendsNotChallenge.forEach(nf -> exerciseRecordRepository.findRecordOfThisWeek(nf.getId())
                .forEach(e -> friendHashMap.put(nf.getNickname(),
                        e.getMatrices()
                                .stream().map(m -> MatrixSetDto.builder()
                                        .latitude(m.getLatitude())
                                        .longitude(m.getLongitude())
                                        .build()
                        )
                        .collect(Collectors.toSet()))));

        List<UserResponseDto.FriendMatrix> friendMatrices = new ArrayList<>();
        for (String friendNickname : friendHashMap.keySet()) {
            User friend = userRepository.findByNickname(friendNickname).orElseThrow(); //예외 처리 예정
            friendMatrices.add(new UserResponseDto.FriendMatrix(friendNickname, friend.getLatitude(), friend.getLongitude(),
                    friendHashMap.get(friendNickname)));
        }

        /*챌린지를 하는 사람들의 matrix 와 정보 (challengeMatrices)*/
        List<UserResponseDto.ChallengeMatrix> challengeMatrices = new ArrayList<>();

        for (User friend : friendsWithChallenge) {
            Set<MatrixSetDto> showMatrices = new HashSet<>();
            Integer challengeNumber = challengeRepository.findCountChallenge(user, friend); // 함께하는 챌린지 수
            String challengeColor = challengeRepository.findChallengesWithFriend(user, friend).get(0).getColor(); // 챌린지 색
            List<ExerciseRecord> recordOfThisWeek = exerciseRecordRepository.findRecordOfThisWeek(friend.getId()); //이번주 기록
            recordOfThisWeek.forEach(e ->
                    e.getMatrices().forEach(m -> showMatrices.add(MatrixSetDto.builder()
                                    .latitude(m.getLatitude())
                                    .longitude(m.getLongitude())
                                    .build())
                            )
            );
            challengeMatrices.add(new UserResponseDto.ChallengeMatrix(
                    friend.getNickname(), challengeNumber, challengeColor, friend.getLatitude(), friend.getLongitude(), showMatrices));
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

        return UserResponseDto.UInfo.builder()
                .nickname(nickname)
                .intro(user.getIntro())
                .build();

    }

}