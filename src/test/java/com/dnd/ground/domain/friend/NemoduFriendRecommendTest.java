package com.dnd.ground.domain.friend;

import com.dnd.ground.common.DataProvider;
import com.dnd.ground.domain.friend.dto.FriendResponseDto;
import com.dnd.ground.domain.friend.service.FriendService;
import com.dnd.ground.domain.user.LoginType;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.UserProperty;
import com.dnd.ground.domain.user.repository.UserRepository;
import com.dnd.ground.domain.user.service.UserService;
import com.dnd.ground.global.util.RequirementUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@DisplayName("친구: 네모두 추천 친구 테스트")
@Transactional
public class NemoduFriendRecommendTest {
    @Autowired
    DataProvider dataProvider;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    @Autowired
    FriendService friendService;

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    static final double LATITUDE = 37.334501;
    static final double LONGITUDE = 126.810133;
    static final int USER_TOTAL_CNT = 50;
    @Value("${picture.path}")
    private String DEFAULT_PATH;

    @Value("${picture.name}")
    private String DEFAULT_NAME;

    /**
     * 회원은 기준점(37.334501, 126.810133)으로부터 인덱스 번호만큼 위로 한 칸씩 이동한다.
     */
    @BeforeEach
    void init() {
        for (int i = 1; i <= USER_TOTAL_CNT; i++) {
            User user = User.builder()
                    .nickname("nick" + i)
                    .email("email" + i + "@gmail.com")
                    .intro("nick" + i + "의 소개 메시지")
                    .created(LocalDateTime.now())
                    .pictureName(DEFAULT_NAME)
                    .picturePath(DEFAULT_PATH)
                    .latitude(LATITUDE + (0.0003740 * i))
                    .longitude(LONGITUDE)
                    .loginType(i % 2 == 0 ? LoginType.APPLE : LoginType.KAKAO)
                    .build();

            UserProperty property = UserProperty.builder()
                    .socialId(i % 2 == 0 ? null : String.valueOf(i))
                    .isExceptRecommend(false)
                    .isShowMine(true)
                    .isShowFriend(true)
                    .isPublicRecord(true)
                    .notiWeekStart(true)
                    .notiWeekEnd(true)
                    .notiFriendRequest(true)
                    .notiFriendAccept(true)
                    .notiChallengeRequest(true)
                    .notiChallengeAccept(true)
                    .notiChallengeStart(true)
                    .notiChallengeCancel(true)
                    .notiChallengeResult(true)
                    .build();

            user.setUserProperty(property);
            userRepository.save(user);
        }
    }

    @Test
    @DisplayName("네모두 추천 친구: 회원 가입 단계 - 오름차순")
    void recommendNemoduFriend_newUser_Asc() throws Exception {
        System.out.println(">>> 네모두 추천 친구: 회원 가입 단계 - 오름차순 <<< 테스트 START");

        //GIVEN
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("latitude", String.valueOf(37.333418));
        map.add("longitude", String.valueOf(126.810133));
        map.add("size", "10");

        //WHEN + THEN (페이지 끝까지 요청 및 검사)
        FriendResponseDto.RecommendResponse result;
        int last = 0;

        do {
            String response = request(map);
            result = mapper.readValue(response, FriendResponseDto.RecommendResponse.class);

            for (FriendResponseDto.FInfo info : result.getInfos()) {
                System.out.println(">>>" + info.toString());
                int idx = Integer.parseInt(info.getNickname().split("nick")[1]);
                assertThat(idx).isGreaterThan(last);
                last = idx;
            }

            if (!result.getIsLast()) {
                map.remove("distance");
                map.add("distance", String.valueOf(result.getOffset()));
            }
        } while (!result.getIsLast());
    }

    @Test
    @DisplayName("네모두 추천 친구: 회원 가입 단계 - 내림차순")
    void recommendNemoduFriend_newUser_Desc() throws Exception {
        System.out.println(">>> 네모두 추천 친구: 회원 가입 단계 - 내림차순 <<< 테스트 START");

        //GIVEN
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("latitude", String.valueOf(83.935693));
        map.add("longitude", String.valueOf(127.821518));
        map.add("size", "10");

        //WHEN + THEN (페이지 끝까지 요청 및 검사)
        FriendResponseDto.RecommendResponse result;
        int last = Integer.MAX_VALUE;

        do {
            String response = request(map);
            result = mapper.readValue(response, FriendResponseDto.RecommendResponse.class);

            for (FriendResponseDto.FInfo info : result.getInfos()) {
                System.out.println(">>>" + info.toString());
                int idx = Integer.parseInt(info.getNickname().split("nick")[1]);
                assertThat(idx).isLessThan(last);
                last = idx;
            }

            if (!result.getIsLast()) {
                map.remove("distance");
                map.add("distance", String.valueOf(result.getOffset()));
            }
        } while (!result.getIsLast());
    }

    @Test
    @DisplayName("네모두 추천 친구: 본인, 친구 제외")
    void recommendNemoduFriend_oldUser() throws Exception {
        System.out.println(">>> 네모두 추천 친구: 본인, 친구 제외 <<< 테스트 START");
        //본인, 내가 친구를 요청한 사람, 나에게 친구를 요청한 사람, 이미 친구인 사람 모두 제외

        //GIVEN
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("nickname", "nick1");
        map.add("latitude", String.valueOf(37.333418));
        map.add("longitude", String.valueOf(126.810133));
        map.add("size", "10");

        List<String> exceptUsers = new ArrayList<>();
        exceptUsers.add("nick1");
        exceptUsers.add("nick2");
        exceptUsers.add("nick11");
        exceptUsers.add("nick24");
        friendService.requestFriend("nick1", "nick2"); //요청 보낸 사람: nick2
        friendService.requestFriend("nick11", "nick1");//요청 받은 사람: nick11
        friendService.requestFriend("nick1", "nick24");//이미 친구: nick24
        friendService.responseFriend("nick24", "nick1", FriendStatus.ACCEPT);

        //WHEN + THEN (페이지 끝까지 요청 및 검사)
        FriendResponseDto.RecommendResponse result;
        int last = 0;

        do {
            String response = request(map);
            result = mapper.readValue(response, FriendResponseDto.RecommendResponse.class);

            for (FriendResponseDto.FInfo info : result.getInfos()) {
                System.out.println(">>>" + info.toString());
                int idx = Integer.parseInt(info.getNickname().split("nick")[1]);
                assertThat(idx).isGreaterThan(last);
                last = idx;

                assertThat(exceptUsers.contains(info.getNickname())).isFalse();
            }

            if (!result.getIsLast()) {
                map.remove("distance");
                map.add("distance", String.valueOf(result.getOffset()));
            }
        } while (!result.getIsLast());
    }

    @Test
    @DisplayName("네모두 추천 친구: 추천 친구 제외 필터 테스트")
    void recommendNemoduFriend_Filter() throws Exception {
        System.out.println(">>> 네모두 추천 친구: 추천 친구 제외 필터 <<< 테스트 START");
        //홀수번째 회원은 친구 추천 제외 옵션이 켜져있다.

        //GIVEN
        for (int i = 1; i <= USER_TOTAL_CNT; i += 2) {
            userService.changeFilterExceptRecommend("nick" + i);
        }

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("latitude", String.valueOf(37.333418));
        map.add("longitude", String.valueOf(126.810133));
        map.add("size", "10");

        //WHEN + THEN (페이지 끝까지 요청 및 검사)
        FriendResponseDto.RecommendResponse result;
        int last = 0;

        do {
            String response = request(map);
            result = mapper.readValue(response, FriendResponseDto.RecommendResponse.class);

            for (FriendResponseDto.FInfo info : result.getInfos()) {
                System.out.println(">>>" + info.toString());
                int idx = Integer.parseInt(info.getNickname().split("nick")[1]);
                assertThat(idx).isGreaterThan(last);
                assertThat(idx % 2 == 0).isTrue();
                last = idx;
            }

            if (!result.getIsLast()) {
                map.remove("distance");
                map.add("distance", String.valueOf(result.getOffset()));
            }
        } while (!result.getIsLast());
    }

    @Test
    @DisplayName("네모두 추천 친구: 추천 친구 제외 필터 + 본인, 친구 제외 테스트")
    void recommendNemoduFriend_Filter_WITH_FRIEND() throws Exception {
        System.out.println(">>> 네모두 추천 친구: 추천 친구 제외 필터 + 친구 제외 <<< 테스트 START");
        //홀수번째 회원은 친구 추천 제외 옵션이 켜져있다.

        //GIVEN
        for (int i = 1; i <= USER_TOTAL_CNT; i += 2) {
            userService.changeFilterExceptRecommend("nick" + i);
        }

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("nickname", "nick1");
        map.add("latitude", String.valueOf(37.333418));
        map.add("longitude", String.valueOf(126.810133));
        map.add("size", "10");

        List<String> exceptUsers = new ArrayList<>();
        exceptUsers.add("nick1");
        exceptUsers.add("nick2");
        exceptUsers.add("nick11");
        exceptUsers.add("nick14");
        exceptUsers.add("nick24");
        friendService.requestFriend("nick1", "nick2"); //요청 보낸 사람: nick2
        friendService.requestFriend("nick11", "nick1");//요청 받은 사람: nick11
        friendService.requestFriend("nick14", "nick1");//요청 받은 사람: nick14
        friendService.requestFriend("nick1", "nick24");//이미 친구: nick24
        friendService.responseFriend("nick24", "nick1", FriendStatus.ACCEPT);

        //WHEN + THEN (페이지 끝까지 요청 및 검사)
        FriendResponseDto.RecommendResponse result;
        int last = 0;

        do {
            String response = request(map);
            result = mapper.readValue(response, FriendResponseDto.RecommendResponse.class);

            for (FriendResponseDto.FInfo info : result.getInfos()) {
                System.out.println(">>>" + info.toString());
                int idx = Integer.parseInt(info.getNickname().split("nick")[1]);

                assertThat(idx).isGreaterThan(last);
                assertThat(idx % 2 == 0).isTrue();
                assertThat(exceptUsers.contains(info.getNickname())).isFalse();

                last = idx;
            }

            if (!result.getIsLast()) {
                map.remove("distance");
                map.add("distance", String.valueOf(result.getOffset()));
            }
        } while (!result.getIsLast());
    }

    @Test
    @DisplayName("네모두 추천 친구: 삭제된 회원 제외")
    void recommendNemoduFriend_No_DeleteUser() throws Exception {
        System.out.println(">>> 네모두 추천 친구: 삭제된 회원 제외 <<< 테스트 START");

        //GIVEN
        String deleteUserNickname = RequirementUtil.getDeleteUser().getNickname();

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("latitude", String.valueOf(37.333418));
        map.add("longitude", String.valueOf(126.810133));
        map.add("size", "10");

        //WHEN + THEN (페이지 끝까지 요청 및 검사)
        FriendResponseDto.RecommendResponse result;

        do {
            String response = request(map);
            result = mapper.readValue(response, FriendResponseDto.RecommendResponse.class);

            for (FriendResponseDto.FInfo info : result.getInfos()) {
                System.out.println(">>>" + info.toString());
                assertThat(info.getNickname()).isNotEqualTo(deleteUserNickname);
            }

            if (!result.getIsLast()) {
                map.remove("distance");
                map.add("distance", String.valueOf(result.getOffset()));
            }
        } while (!result.getIsLast());
    }

    String request(MultiValueMap<String, String> map) throws Exception {
        return mvc
                .perform(get("/friend/recommend")
                        .params(map)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }
}
