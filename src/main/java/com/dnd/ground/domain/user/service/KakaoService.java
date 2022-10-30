package com.dnd.ground.domain.user.service;

import com.dnd.ground.domain.friend.service.FriendService;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.dto.KakaoDto;
import com.dnd.ground.domain.user.repository.UserRepository;
import com.dnd.ground.global.exception.CNotFoundException;
import com.dnd.ground.global.exception.CommonErrorCode;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * @description 카카오를 비롯한 회원 정보와 관련한 서비스
 * @author  박찬호
 * @since   2022-08-23
 * @updated 1. 카카오 친구 목록 조회 API 구현
 *          - 2022.10.29 박찬호
 */

@RequiredArgsConstructor
@Service
public class KakaoService {
    WebClient webClient;
    private final UserRepository userRepository;
    private final FriendService friendService;

    @Value("${kakao.REST_KEY}")
    private String REST_API_KEY;

    /*로컬과 배포 환경의 Redirect URI가 다른 점 확인!*/
    @Value("${kakao.REDIRECT_URI}")
    private String REDIRECT_URI;

    @PostConstruct
    public void initWebClient() {
        webClient = WebClient.create();
    }

    /**deprecated
    /*카카오 토큰 발급*/
    public Map<String, String> kakaoLogin(String code) throws NullPointerException {
        //토큰을 받기 위한 HTTP Body 생성
        MultiValueMap<String, String> getTokenBody = new LinkedMultiValueMap<>();
        getTokenBody.add("grant_type", "authorization_code");
        getTokenBody.add("client_id", REST_API_KEY);
        getTokenBody.add("redirect_uri", REDIRECT_URI);
        getTokenBody.add("code", code);

        //카카오 토큰 발급 API 호출
        KakaoDto.Token token = webClient.post()
                .uri("https://kauth.kakao.com/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(getTokenBody))
                .retrieve()
                .bodyToMono(KakaoDto.Token.class)
                .block();

        Map<String, String> tokens = new HashMap<>();
        tokens.put("Access-Token", token.getAccess_token());
        tokens.put("Refresh-Token", token.getRefresh_token());

        return tokens;
    }

     /*카카오 엑세스 토큰 정보 확인*/
    public KakaoDto.TokenInfo getTokenInfo(String token) {
        return webClient.get()
                .uri("https://kapi.kakao.com/v1/user/access_token_info")
                .header("Authorization","Bearer " + token)
                .retrieve()
                .bodyToMono(KakaoDto.TokenInfo.class)
                .block();
    }

    /*사용자 정보 확인*/
    public KakaoDto.UserInfo getUserInfo(String token) throws ParseException {
        String userKakaoInfo = webClient.get()
                .uri("https://kapi.kakao.com/v2/user/me")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        JSONParser jsonParser = new JSONParser();

        JSONObject jsonObject = (JSONObject) jsonParser.parse(userKakaoInfo); //Cast: String -> Json Object
        long kakaoId = (long) jsonObject.get("id"); // 카카오 회원번호 추출

        jsonObject = (JSONObject) jsonObject.get("kakao_account"); // 필요한 회원 정보가 있는 Object 분리
        JSONObject pictureObject = (JSONObject) jsonObject.get("profile"); //프로필 사진과 관련한 Object 분리

        return KakaoDto.UserInfo.builder()
                .id(kakaoId)
                .email((String) jsonObject.get("email"))
                .pictureName("kakao/"+kakaoId)
                .picturePath((String) pictureObject.get("profile_image_url"))
                .build();
    }

    /*카카오 친구 목록 조회*/
    public KakaoDto.kakaoFriendResponse getKakaoFriends(String token, String nickname, Integer offset) {
        WebClient webClient = WebClient.create();

        User user = userRepository.findByNickname(nickname).orElseThrow(
                () -> new CNotFoundException(CommonErrorCode.NOT_FOUND_USER)
        );

        final int PAGING_NUMBER = 15;

        KakaoDto.kakaoFriendResponse response = new KakaoDto.kakaoFriendResponse();
        int nextOffset = 0;
        boolean isLast = false;

        //카카오 친구 목록 조회
        KakaoDto.FriendsInfoFromKakao responseFromKakao = webClient.get()
                .uri(UriComponentsBuilder.newInstance()
                        .scheme("https")
                        .host("kapi.kakao.com")
                        .path("/v1/api/talk/friends")
                        .queryParam("offset", offset)
                        .queryParam("limit", 100)
                        .toUriString())
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(KakaoDto.FriendsInfoFromKakao.class)
                .block();

        try {
            List<KakaoDto.FriendsInfoFromKakao.KakaoFriend> elements = Objects.requireNonNull(responseFromKakao).getElements();

            for (int i=0; i<elements.size(); i++) {
                KakaoDto.FriendsInfoFromKakao.KakaoFriend kakaoFriend = elements.get(i);
                Optional<User> kakaoUserInNemoduOpt = userRepository.findByKakaoId(kakaoFriend.getId());

                //네모두 회원인 카카오 친구는 친구 추천에 포함
                if (kakaoUserInNemoduOpt.isPresent()) {
                    User kakaoUserInNemodu = kakaoUserInNemoduOpt.get();
                    response.getFriendsInfo().add(
                            KakaoDto.kakaoFriendResponse.FriendsInfo.builder()
                                    .nickname(kakaoUserInNemodu.getNickname())
                                    .kakaoName(kakaoFriend.getProfile_nickname())
                                    .status(friendService.getFriendStatus(user, kakaoUserInNemodu))
                                    .picturePath(kakaoUserInNemodu.getPicturePath())
                                    .build()
                    );
                }

                //페이징 개수만큼 친구 목록이 차면, 다음 offset을 저장하고 넘겨준다.
                if (response.getFriendsInfo().size() >= PAGING_NUMBER) {
                    nextOffset = i;
                    break;
                }
            }
        }
        catch (NullPointerException e) { //카카오 친구목록 조회가 안되는 경우
            response.setNextOffset(0);
            response.setSize(0);
            response.setIsLast(true);
            return response;
        }

        //페이징 개수만큼 친구 목록이 안차면 다음은 없다.
        if (response.getFriendsInfo().size() < PAGING_NUMBER){
            isLast = true;
            nextOffset=0;
        }

        response.setNextOffset(nextOffset);
        response.setSize(response.getFriendsInfo().size());
        response.setIsLast(isLast);

        return response;
    }
}