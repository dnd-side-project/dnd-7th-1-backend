package com.dnd.ground.domain.friend;

import com.dnd.ground.common.DataProvider;
import com.dnd.ground.domain.friend.dto.FriendRequestDto;
import com.dnd.ground.domain.friend.dto.FriendResponseDto;
import com.dnd.ground.domain.friend.repository.FriendRepository;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.repository.UserRepository;
import com.dnd.ground.global.exception.ErrorResponse;
import com.dnd.ground.global.exception.ExceptionCodeSet;
import com.dnd.ground.global.exception.UserException;
import com.dnd.ground.global.notification.PushNotification;
import com.dnd.ground.global.notification.PushNotificationType;
import com.dnd.ground.global.notification.repository.NotificationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@DisplayName("친구: 친구 요청 및 응답 테스트")
@Transactional
class FriendCreateTest {

    @Autowired
    DataProvider dataProvider;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    @Autowired
    FriendRepository friendRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    NotificationRepository notificationRepository;

    @BeforeEach
    public void init() {
        dataProvider.createUser(10);
    }


    @Nested
    @DisplayName("친구 요청하기")
    class RequestFriendTest {

        @Test
        @DisplayName("친구 요청 성공: 정상적으로 동작")
        void requestFriend_Success() throws Exception {
            System.out.println(">>> 친구 요청 성공: 정상적으로 동작 <<< 테스트 START");

            //GIVEN
            String nickname = "nick1";
            User user = userRepository.findByNickname(nickname)
                    .orElseThrow(() -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));

            String friendNickname = "nick2";
            User friend = userRepository.findByNickname(friendNickname)
                    .orElseThrow(() -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));

            FriendRequestDto.Request request = new FriendRequestDto.Request(nickname, friendNickname);
            String requestBody = mapper.writeValueAsString(request);

            //WHEN
            String response = mvc
                    .perform(post("/friend/request")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                    )
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            //THEN
            Boolean result = mapper.readValue(response, Boolean.class);
            assertThat(result).isTrue();


            List<Friend> friendRelations = friendRepository.findFriendRelation(user, friend);
            assertThat(friendRelations.size()).isEqualTo(1);

            Friend friendRelation = friendRelations.get(0);
            assertThat(friendRelation.getUser()).isEqualTo(user);
            assertThat(friendRelation.getFriend()).isEqualTo(friend);
            assertThat(friendRelation.getStatus()).isEqualTo(FriendStatus.WAIT);


            //푸시 알람 여부
            /**
             * 푸시 알람 여부를 테스트하기 위해선, 실제 FCM 토큰을 저장해야 한다.
             * 재발송 처리로 인해 바로 DB에 저장이 되지 않기 때문에 테스트는 실패한다.
             */
            Optional<PushNotification> notificationOpt = notificationRepository.findByUser(friend);
            assertThat(notificationOpt.isPresent()).isTrue();

            PushNotification notification = notificationOpt.get();
            assertThat(notification.getUser()).isEqualTo(friend);
            assertThat(notification.getType()).isEqualTo(PushNotificationType.FRIEND);
        }

        @Test
        @DisplayName("친구 요청 실패: 친구 관계가 존재할 때")
        void requestFriend_Fail_DuplicateRequest() throws Exception {
            System.out.println(">>> 친구 요청 실패: 친구 관계가 존재할 때 <<< 테스트 START");

            //GIVEN
            String nickname = "nick1";
            String friendNickname = "nick2";

            FriendRequestDto.Request request = new FriendRequestDto.Request(nickname, friendNickname);
            String requestBody = mapper.writeValueAsString(request);

            mvc
                    .perform(post("/friend/request")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                    )
                    .andExpect(status().isOk());

            //WHEN
            String response = mvc
                    .perform(post("/friend/request")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                    )
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            //THEN
            ErrorResponse result = mapper.readValue(response, ErrorResponse.class);
            assertThat(result.getCode()).isEqualTo(ExceptionCodeSet.FRIEND_DUPL.getCode());
        }

        @Test
        @DisplayName("친구 요청 실패: 없는 회원에게 요청")
        void requestFriend_Fail_InvalidFriend() throws Exception {
            System.out.println(">>> 친구 요청 실패: 없는 회원에게 요청 <<< 테스트 START");

            //GIVEN
            String nickname = "nick1";
            String invalidNickname = "-";

            FriendRequestDto.Request request = new FriendRequestDto.Request(nickname, invalidNickname);
            String requestBody = mapper.writeValueAsString(request);

            //WHEN
            String response = mvc
                    .perform(post("/friend/request")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                    )
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            //THEN
            ErrorResponse result = mapper.readValue(response, ErrorResponse.class);
            assertThat(result.getCode()).isEqualTo(ExceptionCodeSet.FRIEND_NOT_FOUND.getCode());
        }
    }

    @Nested
    @DisplayName("친구 응답하기")
    class ResponseFriend {

        @Test
        @DisplayName("친구 응답 성공: 수락")
        public void responseFriend_Success_Accept() throws Exception {
            System.out.println(">>> 친구 응답 성공: 수락 <<< 테스트 START");

            //GIVEN
            String nickname = "nick1";
            User user = userRepository.findByNickname(nickname)
                    .orElseThrow(() -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));

            String friendNickname = "nick2";
            User friend = userRepository.findByNickname(friendNickname)
                    .orElseThrow(() -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));

            FriendRequestDto.Request request = new FriendRequestDto.Request(nickname, friendNickname);
            String requestBody = mapper.writeValueAsString(request);

            mvc
                    .perform(post("/friend/request")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                    )
                    .andExpect(status().isOk());

            //WHEN
            FriendRequestDto.Response responseRequest = new FriendRequestDto.Response(friendNickname, nickname, FriendStatus.ACCEPT);
            String responseRequestBody = mapper.writeValueAsString(responseRequest);

            String response = mvc
                    .perform(post("/friend/response")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(responseRequestBody)
                    )
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            //THEN
            FriendResponseDto.ResponseResult result = mapper.readValue(response, FriendResponseDto.ResponseResult.class);
            assertThat(result.getUserNickname()).isEqualTo(nickname);
            assertThat(result.getFriendNickname()).isEqualTo(friendNickname);
            assertThat(result.getStatus()).isEqualTo(FriendStatus.ACCEPT);

            List<Friend> friendRelation = friendRepository.findFriendRelation(user, friend);
            assertThat(friendRelation.size()).isEqualTo(2);

            for (Friend f : friendRelation) {
                assert (f.getUser() == user && f.getFriend() == friend && f.getStatus() == FriendStatus.ACCEPT)
                        || (f.getFriend() == user && f.getUser() == friend && f.getStatus() == FriendStatus.ACCEPT);
            }
        }

        @Test
        @DisplayName("친구 응답 성공: 거절")
        public void responseFriend_Success_Reject() throws Exception {
            System.out.println(">>> 친구 응답 성공: 거절 <<< 테스트 START");

            //GIVEN
            String nickname = "nick1";
            User user = userRepository.findByNickname(nickname)
                    .orElseThrow(() -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));

            String friendNickname = "nick2";
            User friend = userRepository.findByNickname(friendNickname)
                    .orElseThrow(() -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));

            FriendRequestDto.Request request = new FriendRequestDto.Request(nickname, friendNickname);
            String requestBody = mapper.writeValueAsString(request);

            mvc
                    .perform(post("/friend/request")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                    )
                    .andExpect(status().isOk());

            //WHEN
            FriendRequestDto.Response responseRequest = new FriendRequestDto.Response(friendNickname, nickname, FriendStatus.REJECT);
            String responseRequestBody = mapper.writeValueAsString(responseRequest);

            String response = mvc
                    .perform(post("/friend/response")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(responseRequestBody)
                    )
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            //THEN
            FriendResponseDto.ResponseResult result = mapper.readValue(response, FriendResponseDto.ResponseResult.class);
            assertThat(result.getUserNickname()).isEqualTo(nickname);
            assertThat(result.getFriendNickname()).isEqualTo(friendNickname);

            List<Friend> friendRelation = friendRepository.findFriendRelation(user, friend);
            assertThat(friendRelation.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("친구 응답 실패: 친구 요청 없이 응답")
        void requestFriend_Fail_InvalidFriend() throws Exception {
            System.out.println(">>> 친구 응답 실패: 친구 요청 없이 응답 <<< 테스트 START");

            //GIVEN
            String nickname = "nick1";
            String friendNickname = "nick2";

            //WHEN
            FriendRequestDto.Response responseRequest = new FriendRequestDto.Response(friendNickname, nickname, FriendStatus.ACCEPT);
            String responseRequestBody = mapper.writeValueAsString(responseRequest);

            String response = mvc
                    .perform(post("/friend/response")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(responseRequestBody)
                    )
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            //THEN
            ErrorResponse result = mapper.readValue(response, ErrorResponse.class);
            assertThat(result.getCode()).isEqualTo(ExceptionCodeSet.FRIEND_NOT_FOUND_REQ.getCode());
        }
    }
}