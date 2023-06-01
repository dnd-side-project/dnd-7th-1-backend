package com.dnd.ground.domain.challenge;

import com.dnd.ground.common.DataProvider;
import com.dnd.ground.domain.challenge.dto.ChallengeCreateRequestDto;
import com.dnd.ground.domain.challenge.dto.ChallengeResponseDto;
import com.dnd.ground.domain.challenge.repository.ChallengeRepository;
import com.dnd.ground.domain.challenge.service.ChallengeService;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.repository.UserRepository;
import com.dnd.ground.global.exception.ExceptionCodeSet;
import com.dnd.ground.global.exception.UserException;
import com.dnd.ground.global.util.UuidUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@DisplayName("챌린지: 챌린지 목록 조회 테스트")
@Transactional
public class ChallengeListReadTest {
    @Autowired
    DataProvider dataProvider;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ChallengeRepository challengeRepository;

    @Autowired
    ChallengeService challengeService;

    @BeforeEach
    public void init() {
        dataProvider.createUser(10);
    }

    @Nested
    @DisplayName("초대 받은 챌린지 목록 조회")
    class ReadInviteChallenge {
        @Test
        @DisplayName("초대 받은 챌린지 목록 조회 성공")
        void readInviteChallenge_Success() throws Exception {
            System.out.println(">>> 초대 받은 챌린지 목록 조회 성공 <<< 테스트 START");

            //GIVEN
            String masterNickname = "nick1";
            User master = userRepository.findByNickname("nick1")
                    .orElseThrow(() -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));

            String member1Nickname = "nick2";
            String member2Nickname = "nick3";

            Set<String> members = new HashSet<>();
            members.add(member1Nickname);
            members.add(member2Nickname);

            String challengeName = "초대 받은 챌린지 목록 조회 테스트";
            String message = "초대 받은 챌린지 목록 조회 테스트입니다.";
            LocalDateTime started = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIN);
            ChallengeType type = ChallengeType.WIDEN;

            challengeService.createChallenge(new ChallengeCreateRequestDto(masterNickname, challengeName, message, started, type, members));


            String challenge2Name = "nick2가 포함되지 않은 챌린지 테스트";
            String message2 = "nick2가 포함되지 않은 챌린지입니다.";
            members.remove(member1Nickname);
            challengeService.createChallenge(new ChallengeCreateRequestDto(masterNickname, challenge2Name, message2, started, type, members));

            //WHEN
            String response = mvc
                    .perform(get("/challenge/invite")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("nickname", member1Nickname)
                    )
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);

            //THEN
            List<ChallengeResponseDto.Invite> result = Arrays.asList(mapper.readValue(response, ChallengeResponseDto.Invite[].class));
            assertThat(result.size()).isEqualTo(1);

            ChallengeResponseDto.Invite inviteChallenge = result.get(0);

            assertThat(inviteChallenge.getInviterNickname()).isEqualTo(masterNickname);
            assertThat(inviteChallenge.getName()).isEqualTo(challengeName);
            assertThat(inviteChallenge.getPicturePath()).isEqualTo(master.getPicturePath());

            Optional<Challenge> challengeOpt = challengeRepository.findByUuid(UuidUtil.hexToBytes(inviteChallenge.getUuid()));
            assertThat(challengeOpt.isPresent()).isTrue();
        }

        @Test
        @DisplayName("초대 받은 챌린지 목록 조회 성공: 아무것도 초대 받지 않았을 때")
        void readInviteChallenge_Fail_NotInviteMember() throws Exception {
            System.out.println(">>> 초대 받은 챌린지 목록 조회 성공: 아무것도 초대 받지 않았을 때 <<< 테스트 START");

            //GIVEN
            String nickname = "nick1";

            //WHEN
            String response = mvc
                    .perform(get("/challenge/invite")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("nickname", nickname)
                    )
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            //THEN
            List<ChallengeResponseDto.Invite> result = Arrays.asList(mapper.readValue(response, ChallengeResponseDto.Invite[].class));
            assertThat(result.isEmpty()).isTrue();
        }
    }
}
