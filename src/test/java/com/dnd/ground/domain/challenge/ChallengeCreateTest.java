package com.dnd.ground.domain.challenge;

import com.dnd.ground.common.DataProvider;
import com.dnd.ground.domain.challenge.dto.ChallengeCreateRequestDto;
import com.dnd.ground.domain.challenge.dto.ChallengeCreateResponseDto;
import com.dnd.ground.domain.challenge.repository.ChallengeRepository;
import com.dnd.ground.domain.challenge.repository.UserChallengeRepository;
import com.dnd.ground.domain.challenge.service.ChallengeService;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.dto.UserResponseDto;
import com.dnd.ground.domain.user.repository.UserRepository;
import com.dnd.ground.global.exception.ErrorResponse;
import com.dnd.ground.global.exception.ExceptionCodeSet;
import com.dnd.ground.global.exception.UserException;
import com.dnd.ground.global.notification.PushNotification;
import com.dnd.ground.global.notification.PushNotificationType;
import com.dnd.ground.global.notification.repository.NotificationRepository;
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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@DisplayName("챌린지: 챌린지 생성 및 응답 테스트")
@Transactional
class ChallengeCreateTest {

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

    @Autowired
    UserChallengeRepository userChallengeRepository;

    @Autowired
    NotificationRepository notificationRepository;

    @BeforeEach
    public void init() {
        dataProvider.createUser(10);
    }

    @Nested
    @DisplayName("챌린지 생성하기")
    class CreateChallenge {

        @Test
        @DisplayName("챌린지 생성 성공")
        void createChallenge_Success() throws Exception {
            System.out.println(">>> 챌린지 생성 성공 <<< 테스트 START");

            //GIVEN
            String masterNickname = "nick1";
            User master = userRepository.findByNickname(masterNickname)
                    .orElseThrow(() -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));

            String member1Nickname = "nick2";
            User member1 = userRepository.findByNickname(member1Nickname)
                    .orElseThrow(() -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));

            String member2Nickname = "nick3";
            User member2 = userRepository.findByNickname(member2Nickname)
                    .orElseThrow(() -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));

            String challengeName = "챌린지 생성 테스트";
            String message = "챌린지 생성 테스트입니다.";
            LocalDateTime started = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIN);
            LocalDateTime sunday = ChallengeService.getSunday(started);
            ChallengeType type = ChallengeType.ACCUMULATE;

            Set<String> memberNicknames = new HashSet<>();
            memberNicknames.add(member1Nickname);
            memberNicknames.add(member2Nickname);

            //WHEN
            ChallengeCreateRequestDto request = new ChallengeCreateRequestDto(masterNickname, challengeName, message, started, type, memberNicknames);
            String requestBody = mapper.writeValueAsString(request);

            String response = mvc
                    .perform(post("/challenge/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                    )
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);

            //THEN
            ChallengeCreateResponseDto result = mapper.readValue(response, ChallengeCreateResponseDto.class);
            assertThat(result.getMessage()).isEqualTo(message);
            assertThat(result.getStarted()).isEqualTo(started);
            assertThat(result.getEnded()).isEqualTo(sunday);
            assertThat(result.getExceptMemberCount()).isEqualTo(0);
            assertThat(result.getExceptMembers().isEmpty()).isTrue();

            List<UserResponseDto.UInfo> members = result.getMembers();
            for (UserResponseDto.UInfo member : members) {
                assertThat(memberNicknames.contains(member.getNickname())).isTrue();
            }

            //챌린지 생성 확인
            Optional<byte[]> challengeUuidOpt = challengeRepository.findUUIDByName(challengeName);
            assertThat(challengeUuidOpt.isPresent()).isTrue(); //UUID 검증

            Optional<Challenge> challengeOpt = challengeRepository.findByUuid(challengeUuidOpt.get());
            assertThat(challengeOpt.isPresent()).isTrue();
            Challenge challenge = challengeOpt.get();

            assertThat(challenge.getStarted()).isEqualTo(started);
            assertThat(challenge.getEnded()).isEqualTo(sunday);
            assertThat(challenge.getMessage()).isEqualTo(message);
            assertThat(challenge.getType()).isEqualTo(type);
            assertThat(challenge.getName()).isEqualTo(challengeName);

            //멤버 초대 확인
            List<UserChallenge> ucs = userChallengeRepository.findByChallenge(challenge);
            assertThat(ucs.size()).isEqualTo(3);

            for (UserChallenge uc : ucs) {
                if (uc.getUser() == master) {
                    assertThat(uc.getStatus()).isEqualTo(ChallengeStatus.MASTER);
                } else {
                    assertThat(uc.getStatus()).isEqualTo(ChallengeStatus.WAIT);
                }
            }


            //푸시 알람 여부
            /**
             * 푸시 알람 여부를 테스트하기 위해선, 실제 FCM 토큰을 저장해야 한다.
             * 재발송 처리로 인해 바로 DB에 저장이 되지 않기 때문에 테스트는 실패한다.
             */
            Optional<PushNotification> notificationOpt = notificationRepository.findByUser(member1);
            assertThat(notificationOpt.isPresent()).isTrue();

            PushNotification notification = notificationOpt.get();
            assertThat(notification.getUser()).isEqualTo(member1);
            assertThat(notification.getType()).isEqualTo(PushNotificationType.CHALLENGE);

            notificationOpt = notificationRepository.findByUser(member2);
            assertThat(notificationOpt.isPresent()).isTrue();
            notification = notificationOpt.get();
            assertThat(notification.getUser()).isEqualTo(member2);
            assertThat(notification.getType()).isEqualTo(PushNotificationType.CHALLENGE);
        }

        @Test
        @DisplayName("챌린지 생성 실패: 멤버 수 초과")
        void createChallenge_Fail_ExceedMembers() throws Exception {
            System.out.println(">>> 챌린지 생성 실패: 멤버 수 초과 <<< 테스트 START");

            //GIVEN
            String masterNickname = "nick1";

            String member1Nickname = "nick2";
            String member2Nickname = "nick3";
            String member3Nickname = "nick4";
            String member4Nickname = "nick5";

            String challengeName = "챌린지 생성 실패: 멤버 초과";
            String message = "멤버 수가 최대 4명까지 입니다.";
            LocalDateTime started = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIN);
            ChallengeType type = ChallengeType.ACCUMULATE;

            Set<String> memberNicknames = new HashSet<>();
            memberNicknames.add(member1Nickname);
            memberNicknames.add(member2Nickname);
            memberNicknames.add(member3Nickname);
            memberNicknames.add(member4Nickname);

            //WHEN + THEN
            ChallengeCreateRequestDto request = new ChallengeCreateRequestDto(masterNickname, challengeName, message, started, type, memberNicknames);
            String requestBody = mapper.writeValueAsString(request);

            mvc.perform(post("/challenge/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                    )
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("챌린지 생성 실패: 멤버 없음")
        void createChallenge_Fail_EmptyMember() throws Exception {
            System.out.println(">>> 챌린지 생성 실패: 멤버 없음 <<< 테스트 START");

            //GIVEN
            String masterNickname = "nick1";

            String challengeName = "챌린지 생성 실패: 멤버 없음";
            String message = "멤버 1명은 꼭 필요합니다.";
            LocalDateTime started = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIN);
            ChallengeType type = ChallengeType.ACCUMULATE;

            Set<String> memberNicknames = new HashSet<>();

            //WHEN + THEN
            ChallengeCreateRequestDto request = new ChallengeCreateRequestDto(masterNickname, challengeName, message, started, type, memberNicknames);
            String requestBody = mapper.writeValueAsString(request);

            mvc.perform(post("/challenge/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                    )
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("챌린지 생성 실패: 시작 날짜가 올바르지 않음.")
        void createChallenge_Fail_InvalidStarted() throws Exception {
            System.out.println(">>> 챌린지 생성 실패: 시작 날짜가 올바르지 않음. <<< 테스트 START");

            //GIVEN
            String masterNickname = "nick1";
            String member1Nickname = "nick2";

            String challengeName = "챌린지 생성 실패: 시작 날짜가 올바르지 않음.";
            String message = "내일부터 시작 가능합니다.";
            LocalDateTime started = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
            ChallengeType type = ChallengeType.ACCUMULATE;

            Set<String> memberNicknames = new HashSet<>();
            memberNicknames.add(member1Nickname);

            //WHEN
            ChallengeCreateRequestDto request = new ChallengeCreateRequestDto(masterNickname, challengeName, message, started, type, memberNicknames);
            String requestBody = mapper.writeValueAsString(request);

            String response = mvc
                    .perform(post("/challenge/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                    )
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(StandardCharsets.UTF_8);

            //THEN
            ErrorResponse result = mapper.readValue(response, ErrorResponse.class);
            assertThat(result.getCode()).isEqualTo(ExceptionCodeSet.CHALLENGE_DATE_INVALID.getCode());
        }
    }
}