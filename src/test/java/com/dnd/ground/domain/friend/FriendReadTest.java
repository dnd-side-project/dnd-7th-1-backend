package com.dnd.ground.domain.friend;

import com.dnd.ground.common.DataProvider;
import com.dnd.ground.domain.friend.dto.FriendResponseDto;
import com.dnd.ground.domain.friend.service.FriendService;
import com.dnd.ground.global.exception.ErrorResponse;
import com.dnd.ground.global.exception.ExceptionCodeSet;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@DisplayName("친구: 친구 조회 테스트")
@Transactional
public class FriendReadTest {

    @Autowired
    DataProvider dataProvider;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    @Autowired
    FriendService friendService;

    static final String NICKNAME = "nick1";
    static final List<String> requestMeFriends = new ArrayList<>(); //나에게 요청을 보낸 친구들
    static final List<String> requestFriends = new ArrayList<>(); //내가 요청을 보낸 친구들
    static final List<String> friends = new ArrayList<>(); //친구 상태인 친구들


    @BeforeEach
    public void init() {
        dataProvider.createUser(60);

        //나에게 요청 보내는 친구들 (3 7 9)
        String waitFriend1Nickname = "nick3";
        String waitFriend2Nickname = "nick7";
        String waitFriend3Nickname = "nick9";
        String waitFriend4Nickname = "nick13";
        String waitFriend5Nickname = "nick17";
        String waitFriend6Nickname = "nick19";
        String waitFriend7Nickname = "nick23";
        String waitFriend8Nickname = "nick27";
        String waitFriend9Nickname = "nick29";
        String waitFriend10Nickname = "nick33";
        String waitFriend11Nickname = "nick37";
        String waitFriend12Nickname = "nick39";
        String waitFriend13Nickname = "nick43";
        String waitFriend14Nickname = "nick47";
        String waitFriend15Nickname = "nick49";

        assert friendService.requestFriend(waitFriend1Nickname, NICKNAME);
        assert friendService.requestFriend(waitFriend2Nickname, NICKNAME);
        assert friendService.requestFriend(waitFriend3Nickname, NICKNAME);
        assert friendService.requestFriend(waitFriend4Nickname, NICKNAME);
        assert friendService.requestFriend(waitFriend5Nickname, NICKNAME);
        assert friendService.requestFriend(waitFriend6Nickname, NICKNAME);
        assert friendService.requestFriend(waitFriend7Nickname, NICKNAME);
        assert friendService.requestFriend(waitFriend8Nickname, NICKNAME);
        assert friendService.requestFriend(waitFriend9Nickname, NICKNAME);
        assert friendService.requestFriend(waitFriend10Nickname, NICKNAME);
        assert friendService.requestFriend(waitFriend11Nickname, NICKNAME);
        assert friendService.requestFriend(waitFriend12Nickname, NICKNAME);
        assert friendService.requestFriend(waitFriend13Nickname, NICKNAME);
        assert friendService.requestFriend(waitFriend14Nickname, NICKNAME);
        assert friendService.requestFriend(waitFriend15Nickname, NICKNAME);

        requestMeFriends.add(waitFriend1Nickname);
        requestMeFriends.add(waitFriend2Nickname);
        requestMeFriends.add(waitFriend3Nickname);
        requestMeFriends.add(waitFriend4Nickname);
        requestMeFriends.add(waitFriend5Nickname);
        requestMeFriends.add(waitFriend6Nickname);
        requestMeFriends.add(waitFriend7Nickname);
        requestMeFriends.add(waitFriend8Nickname);
        requestMeFriends.add(waitFriend9Nickname);
        requestMeFriends.add(waitFriend10Nickname);
        requestMeFriends.add(waitFriend11Nickname);
        requestMeFriends.add(waitFriend12Nickname);
        requestMeFriends.add(waitFriend13Nickname);
        requestMeFriends.add(waitFriend14Nickname);
        requestMeFriends.add(waitFriend15Nickname);

        //친구 목록 (ACCEPT) (2 4 8)
        String friend1Nickname = "nick2";
        String friend2Nickname = "nick4";
        String friend3Nickname = "nick8";
        String friend4Nickname = "nick12";
        String friend5Nickname = "nick14";
        String friend6Nickname = "nick18";
        String friend7Nickname = "nick22";
        String friend8Nickname = "nick24";
        String friend9Nickname = "nick28";
        String friend10Nickname = "nick32";
        String friend11Nickname = "nick34";
        String friend12Nickname = "nick38";
        String friend13Nickname = "nick42";
        String friend14Nickname = "nick44";
        String friend15Nickname = "nick48";

        assert friendService.requestFriend(friend1Nickname, NICKNAME);
        assert friendService.requestFriend(friend2Nickname, NICKNAME);
        assert friendService.requestFriend(friend3Nickname, NICKNAME);
        assert friendService.requestFriend(friend4Nickname, NICKNAME);
        assert friendService.requestFriend(friend5Nickname, NICKNAME);
        assert friendService.requestFriend(friend6Nickname, NICKNAME);
        assert friendService.requestFriend(friend7Nickname, NICKNAME);
        assert friendService.requestFriend(friend8Nickname, NICKNAME);
        assert friendService.requestFriend(friend9Nickname, NICKNAME);
        assert friendService.requestFriend(friend10Nickname, NICKNAME);
        assert friendService.requestFriend(friend11Nickname, NICKNAME);
        assert friendService.requestFriend(friend12Nickname, NICKNAME);
        assert friendService.requestFriend(friend13Nickname, NICKNAME);
        assert friendService.requestFriend(friend14Nickname, NICKNAME);
        assert friendService.requestFriend(friend15Nickname, NICKNAME);

        friendService.responseFriend(NICKNAME, friend1Nickname, FriendStatus.ACCEPT);
        friendService.responseFriend(NICKNAME, friend2Nickname, FriendStatus.ACCEPT);
        friendService.responseFriend(NICKNAME, friend3Nickname, FriendStatus.ACCEPT);
        friendService.responseFriend(NICKNAME, friend4Nickname, FriendStatus.ACCEPT);
        friendService.responseFriend(NICKNAME, friend5Nickname, FriendStatus.ACCEPT);
        friendService.responseFriend(NICKNAME, friend6Nickname, FriendStatus.ACCEPT);
        friendService.responseFriend(NICKNAME, friend7Nickname, FriendStatus.ACCEPT);
        friendService.responseFriend(NICKNAME, friend8Nickname, FriendStatus.ACCEPT);
        friendService.responseFriend(NICKNAME, friend9Nickname, FriendStatus.ACCEPT);
        friendService.responseFriend(NICKNAME, friend10Nickname, FriendStatus.ACCEPT);
        friendService.responseFriend(NICKNAME, friend11Nickname, FriendStatus.ACCEPT);
        friendService.responseFriend(NICKNAME, friend12Nickname, FriendStatus.ACCEPT);
        friendService.responseFriend(NICKNAME, friend13Nickname, FriendStatus.ACCEPT);
        friendService.responseFriend(NICKNAME, friend14Nickname, FriendStatus.ACCEPT);
        friendService.responseFriend(NICKNAME, friend15Nickname, FriendStatus.ACCEPT);

        friends.add(friend1Nickname);
        friends.add(friend2Nickname);
        friends.add(friend3Nickname);
        friends.add(friend4Nickname);
        friends.add(friend5Nickname);
        friends.add(friend6Nickname);
        friends.add(friend7Nickname);
        friends.add(friend8Nickname);
        friends.add(friend9Nickname);
        friends.add(friend10Nickname);
        friends.add(friend11Nickname);
        friends.add(friend12Nickname);
        friends.add(friend13Nickname);
        friends.add(friend14Nickname);
        friends.add(friend15Nickname);

        //나에게 요청 한 친구 목록 (1 5 6)
        String friendWait1Nickname = "nick5";
        String friendWait2Nickname = "nick6";
        String friendWait3Nickname = "nick11";
        String friendWait4Nickname = "nick15";
        String friendWait5Nickname = "nick16";
        String friendWait6Nickname = "nick21";
        String friendWait7Nickname = "nick25";
        String friendWait8Nickname = "nick26";
        String friendWait9Nickname = "nick31";
        String friendWait10Nickname = "nick35";
        String friendWait11Nickname = "nick36";
        String friendWait12Nickname = "nick41";
        String friendWait13Nickname = "nick45";
        String friendWait14Nickname = "nick46";
        String friendWait15Nickname = "nick51";

        assert friendService.requestFriend(NICKNAME, friendWait1Nickname);
        assert friendService.requestFriend(NICKNAME, friendWait2Nickname);
        assert friendService.requestFriend(NICKNAME, friendWait3Nickname);
        assert friendService.requestFriend(NICKNAME, friendWait4Nickname);
        assert friendService.requestFriend(NICKNAME, friendWait5Nickname);
        assert friendService.requestFriend(NICKNAME, friendWait6Nickname);
        assert friendService.requestFriend(NICKNAME, friendWait7Nickname);
        assert friendService.requestFriend(NICKNAME, friendWait8Nickname);
        assert friendService.requestFriend(NICKNAME, friendWait9Nickname);
        assert friendService.requestFriend(NICKNAME, friendWait10Nickname);
        assert friendService.requestFriend(NICKNAME, friendWait11Nickname);
        assert friendService.requestFriend(NICKNAME, friendWait12Nickname);
        assert friendService.requestFriend(NICKNAME, friendWait13Nickname);
        assert friendService.requestFriend(NICKNAME, friendWait14Nickname);
        assert friendService.requestFriend(NICKNAME, friendWait15Nickname);

        requestFriends.add(friendWait1Nickname);
        requestFriends.add(friendWait2Nickname);
        requestFriends.add(friendWait3Nickname);
        requestFriends.add(friendWait4Nickname);
        requestFriends.add(friendWait5Nickname);
        requestFriends.add(friendWait6Nickname);
        requestFriends.add(friendWait7Nickname);
        requestFriends.add(friendWait8Nickname);
        requestFriends.add(friendWait9Nickname);
        requestFriends.add(friendWait10Nickname);
        requestFriends.add(friendWait11Nickname);
        requestFriends.add(friendWait12Nickname);
        requestFriends.add(friendWait13Nickname);
        requestFriends.add(friendWait14Nickname);
        requestFriends.add(friendWait15Nickname);
    }

    @Nested
    @DisplayName("요청 받은 친구 목록 조회")
    class FriendWaitList {
        @Test
        @DisplayName("요청 받은 친구 목록 조회 성공")
        void readFriendWaitList_Success() throws Exception {
            System.out.println(">>> 요청 받은 친구 목록 조회 성공 <<< 테스트 START");

            //GIVEN
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("nickname", NICKNAME);
            params.add("size", "5");

            //WHEN + THEN (페이지 끝까지)
            long lastOffset = Long.MAX_VALUE;
            FriendResponseDto result;
            do {
                String response = mvc
                        .perform(get("/friend/receive")
                                .contentType(MediaType.APPLICATION_JSON)
                                .params(params)
                        )
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

                result = mapper.readValue(response, FriendResponseDto.class);

                List<FriendResponseDto.FInfo> infos = result.getInfos();
                for (FriendResponseDto.FInfo info : infos) {
                    System.out.println(">>> " + info.toString());

                    assertThat(requestMeFriends.contains(info.getNickname())).isTrue();
                    assertThat(requestFriends.contains(info.getNickname())).isFalse();
                    assertThat(friends.contains(info.getNickname())).isFalse();
                }

                if (!result.getIsLast()) {
                    params.remove("offset");
                    params.add("offset", String.valueOf(result.getOffset()));
                    assertThat(result.getOffset()).isLessThan(lastOffset); //내림차순 정렬 확인
                    lastOffset = result.getOffset();
                }
            } while (!result.getIsLast());
        }

        @Test
        @DisplayName("요청 받은 친구 목록 실패: 유효하지 않은 닉네임")
        void readFriendWaitList_Fail_InvalidNickname() throws Exception {
            System.out.println(">>> 요청 받은 친구 목록 실패: 유효하지 않은 닉네임 <<< 테스트 START");

            //GIVEN
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("nickname", "-");
            params.add("size", "5");

            //WHEN
            String response = mvc
                    .perform(get("/friend/receive")
                            .contentType(MediaType.APPLICATION_JSON)
                            .params(params)
                    )
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            //THEN
            ErrorResponse result = mapper.readValue(response, ErrorResponse.class);
            assertThat(result.getCode()).isEqualTo(ExceptionCodeSet.USER_NOT_FOUND.getCode());
        }

        @Test
        @DisplayName("요청 받은 친구 목록 실패: 필수 파라미터 누락")
        void readFriendWaitList_Fail_EmptyRequiredParam() throws Exception {
            System.out.println(">>> 요청 받은 친구 목록 실패: 필수 파라미터 누락 <<< 테스트 START");

            //GIVEN
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("nickname", NICKNAME);

            //WHEN + THEN
            mvc.perform(get("/friend/receive")
                            .contentType(MediaType.APPLICATION_JSON)
                            .params(params)
                    )
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("친구 목록 조회")
    class readFriendList {

        @Test
        @DisplayName("친구 목록 조회 성공")
        void readFriendList_Success() throws Exception {
            System.out.println(">>> 친구 목록 조회 성공 <<< 테스트 START");

            //GIVEN
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("nickname", NICKNAME);
            params.add("size", "5");

            //WHEN + THEN (페이지 끝까지)
            long lastOffset = Long.MAX_VALUE;
            FriendResponseDto result;
            do {
                String response = mvc
                        .perform(get("/friend/list")
                                .contentType(MediaType.APPLICATION_JSON)
                                .params(params)
                        )
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

                result = mapper.readValue(response, FriendResponseDto.class);

                List<FriendResponseDto.FInfo> infos = result.getInfos();
                for (FriendResponseDto.FInfo info : infos) {
                    System.out.println(">>> " + info.toString());

                    assertThat(friends.contains(info.getNickname())).isTrue();
                    assertThat(requestMeFriends.contains(info.getNickname())).isFalse();
                    assertThat(requestFriends.contains(info.getNickname())).isFalse();
                }

                if (!result.getIsLast()) {
                    params.remove("offset");
                    params.add("offset", String.valueOf(result.getOffset()));
                    assertThat(result.getOffset()).isLessThan(lastOffset); //내림차순 정렬 확인
                    lastOffset = result.getOffset();
                }
            } while (!result.getIsLast());
        }

        @Test
        @DisplayName("친구 목록 실패: 유효하지 않은 닉네임")
        void readFriendList_Fail_InvalidNickname() throws Exception {
            System.out.println(">>> 친구 목록 실패: 유효하지 않은 닉네임 <<< 테스트 START");

            //GIVEN
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("nickname", "-");
            params.add("size", "5");

            //WHEN
            String response = mvc
                    .perform(get("/friend/list")
                            .contentType(MediaType.APPLICATION_JSON)
                            .params(params)
                    )
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            //THEN
            ErrorResponse result = mapper.readValue(response, ErrorResponse.class);
            assertThat(result.getCode()).isEqualTo(ExceptionCodeSet.USER_NOT_FOUND.getCode());
        }

        @Test
        @DisplayName("친구 목록 조회 실패: 필수 파라미터 누락")
        void readFriendList_Fail_EmptyRequiredParam() throws Exception {
            System.out.println(">>> 친구 목록 실패: 필수 파라미터 누락 <<< 테스트 START");

            //GIVEN
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("nickname", NICKNAME);

            //WHEN + THEN
            mvc.perform(get("/friend/list")
                            .contentType(MediaType.APPLICATION_JSON)
                            .params(params)
                    )
                    .andExpect(status().isBadRequest());
        }
    }
}
