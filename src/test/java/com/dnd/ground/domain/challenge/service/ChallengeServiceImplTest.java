package com.dnd.ground.domain.challenge.service;

import com.dnd.ground.domain.challenge.UserChallenge;
import com.dnd.ground.domain.challenge.dto.ChallengeCreateRequestDto;
import com.dnd.ground.domain.challenge.repository.ChallengeRepository;
import com.dnd.ground.domain.challenge.repository.UserChallengeRepository;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ChallengeServiceImplTest {

    @Autowired ChallengeService challengeService;
    @Autowired ChallengeRepository challengeRepository;
    @Autowired UserChallengeRepository userChallengeRepository;
    @Autowired UserRepository userRepository;

    public List<User> createUser() {
        User userA = User.builder()
                .id(1L)
                .userName("nameA")
                .nickName("nickA")
                .friends(new ArrayList<>())
                .challenges(new ArrayList<>())
                .build();

        User userB = User.builder()
                .id(2L)
                .userName("nameB")
                .nickName("nickB")
                .friends(new ArrayList<>())
                .challenges(new ArrayList<>())
                .build();

        User userC = User.builder()
                .id(3L)
                .userName("nameC")
                .nickName("nickC")
                .friends(new ArrayList<>())
                .challenges(new ArrayList<>())
                .build();

        userRepository.save(userA);
        userRepository.save(userB);
        userRepository.save(userC);

        return List.of(userA, userB, userC);
    }

    @Test @Transactional
    public void 챌린지_생성_성공() {
        //given
        List<User> users = createUser();
        User userA = users.get(0);
        User userB = users.get(1);
        User userC = users.get(2);


        //when
        ChallengeCreateRequestDto requestWith2 = new ChallengeCreateRequestDto("3명: 챌린지1", "3명이 함께하는 챌린지", "#FFFFFF", Set.of("nickA","nickB","nickC"));
        challengeService.createChallenge(requestWith2);

        ChallengeCreateRequestDto requestWith3 = new ChallengeCreateRequestDto("2명: 챌린지2", "2명이 함께하는 챌린지", "#CCCCCC", Set.of("nickA","nickB"));
        challengeService.createChallenge(requestWith3);

        List<UserChallenge> AChallenges = userChallengeRepository.findByUser(userA); //A의 챌린지 목록
        List<UserChallenge> BChallenges = userChallengeRepository.findByUser(userB); //A의 챌린지 목록
        List<UserChallenge> CChallenges = userChallengeRepository.findByUser(userC); //A의 챌린지 목록


        //then
        Assertions.assertThat(challengeRepository.findAll().size()).isEqualTo(2); //2개의 챌린지 생성 여부
        Assertions.assertThat(userChallengeRepository.findAll().size()).isEqualTo(5); //2개의 챌린지에 참여하는 총 유저 5명

        Assertions.assertThat(AChallenges.size()).isEqualTo(2);
        Assertions.assertThat(BChallenges.size()).isEqualTo(2);
        Assertions.assertThat(CChallenges.size()).isEqualTo(1);
    }

    @Test @Transactional
    public void 챌린지_생성_실패() {
        //given
        createUser();

        //when
        ChallengeCreateRequestDto request = new ChallengeCreateRequestDto("잘못된 닉네임이 들어간 챌린지", "에러나야 함.", "#FFFFFF", Set.of("nickA","nickB","FALSE"));

        //then
        try {
            challengeService.createChallenge(request);
        } catch (Exception e) {
            Assertions.assertThat(e.getMessage()).isEqualTo("No value present");
        }

    }
}