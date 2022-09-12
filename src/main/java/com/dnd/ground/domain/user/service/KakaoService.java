package com.dnd.ground.domain.user.service;

import com.dnd.ground.domain.friend.dto.FriendResponseDto;
import com.dnd.ground.domain.user.dto.KakaoDto;
import com.dnd.ground.domain.user.repository.UserRepository;
import io.netty.resolver.DefaultAddressResolverGroup;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * @description 카카오를 비롯한 회원 정보와 관련한 서비스
 * @author  박찬호
 * @since   2022-08-23
 * @updated 1. 카카오 회원 정보 조회 API 수정
 *          - 2022.09.09 박찬호
 */

@RequiredArgsConstructor
@Service
public class KakaoService {
    WebClient webClient;

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

    /**deprecated
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
//    public void getKakaoFriends(String token, Integer offset) {
//
//        WebClient webClient2 = WebClient.builder().clientConnector(new ReactorClientHttpConnector(HttpClient.create().resolver(DefaultAddressResolverGroup.INSTANCE))).build();
//
//        //내가 보내주는 offset을 기준으로 페이징
//        String block = webClient2.get()
//                .uri(UriBuilder -> UriBuilder.path("https://kapi.kakao.com/v1/api/talk/friends") //uri builder가 문제인듯
//                        .queryParam("offset", offset)
//                        .queryParam("limit", 100)
//                        .build())
//                .header("Authorization", "Bearer " + token)
//                .retrieve()
//                .bodyToMono(String.class)
//                .block();
//
//        System.out.println("**block:" + block);
//    }

}