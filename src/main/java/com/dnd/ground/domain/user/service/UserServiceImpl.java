package com.dnd.ground.domain.user.service;

import com.dnd.ground.domain.exerciseRecord.ExerciseRecord;
import com.dnd.ground.domain.exerciseRecord.Repository.ExerciseRecordRepository;
import com.dnd.ground.domain.matrix.Matrix;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.dto.HomeResponseDto;
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
 * @updated 2022-08-05 / 충돌 방지 복구: 박세헌
 */

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final ExerciseRecordRepository exerciseRecordRepository;

    @Transactional
    public User save(User user){
        return userRepository.save(user);
    }

    public HomeResponseDto showHome(String nickname){
        User user = userRepository.findByNickName(nickname).orElseThrow();  // 예외 처리

        // 유저의 matrix 와 정보 (userMatrix)
        Set<HomeResponseDto.ShowMatrix> userShowMatrices = new HashSet<>();
        HomeResponseDto.UserMatrix userMatrix = new HomeResponseDto.UserMatrix(nickname, userShowMatrices);
        List<ExerciseRecord> userRecordOfThisWeek = exerciseRecordRepository.findRecordOfThisWeek(user.getId());
        if (!userRecordOfThisWeek.isEmpty()){
            List<List<Matrix>> userMatrices = userRecordOfThisWeek.stream().map(e -> e.getMatrices()).collect(Collectors.toList());
            userMatrices.forEach(ms -> ms.forEach(m ->
                    userShowMatrices.add(HomeResponseDto.ShowMatrix.builder().latitude(m.getLatitude()).longitude(m.getLongitude()).build())));
            userMatrix = new HomeResponseDto.UserMatrix(user.getNickName(), userShowMatrices);
        }


        // 챌린지를 안하는 친구들의 matrix 와 정보 (friendMatrices)
        List<User> notChallenges = new ArrayList<>(); // 챌린지를 안하는 친구들(구현 예정)
        Map<String, Set<HomeResponseDto.ShowMatrix>> friendHashMap= new HashMap<>();

        notChallenges.forEach(nf -> exerciseRecordRepository.findRecordOfThisWeek(nf.getId())
                .forEach(e -> friendHashMap.put(nf.getNickName(), e.getMatrices()
                        .stream().map(m -> HomeResponseDto.ShowMatrix.builder().latitude(m.getLatitude()).longitude(m.getLongitude()).build()).collect(Collectors.toSet()))));
        List<HomeResponseDto.FriendMatrix> friendMatrices = new ArrayList<>();
        for (String s : friendHashMap.keySet()) {
            friendMatrices.add(new HomeResponseDto.FriendMatrix(s, friendHashMap.get(s)));
        }


        // 챌린지를 하는 사람들의 matrix 와 정보 (challengeMatrices)
        List<User> challenges = new ArrayList<>(); // 챌린지를 하는 유저들(구현 예정)

        List<HomeResponseDto.ChallengeMatrix> challengeMatrices = new ArrayList<>();
        for (User u : challenges) {
            Integer challengeNumber = 0; // 이 유저의 챌린지 개수 (구현 예정)
            String challengeColor = ""; // 이 유저의 가장 먼저 선정된 챌린지 색깔 (구현 예정)
            Set<HomeResponseDto.ShowMatrix> showMatrices = new HashSet<>();
            List<ExerciseRecord> recordOfThisWeek = exerciseRecordRepository.findRecordOfThisWeek(u.getId());
            recordOfThisWeek.forEach(e -> e.getMatrices().forEach(m -> showMatrices.add(HomeResponseDto.ShowMatrix.builder().latitude(m.getLatitude()).longitude(m.getLongitude()).build())));
            challengeMatrices.add(new HomeResponseDto.ChallengeMatrix(u.getNickName(), challengeNumber, challengeColor, showMatrices));
        }
        return HomeResponseDto.builder().userMatrix(userMatrix).friendMatrices(friendMatrices).challengeMatrices(challengeMatrices).build();
    }
}