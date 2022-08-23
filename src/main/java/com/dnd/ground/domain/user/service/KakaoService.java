package com.dnd.ground.domain.user.service;

import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.dto.KakaoDto;
import com.dnd.ground.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;
import java.util.Optional;

/**
 * @description 카카오와 관련한 서비스 클래스
 * @author  박찬호
 * @since   2022-08-23
 * @updated 1. 인가 코드를 활용한 엑세스 토큰 발급 및 신규 회원 구분 API 구현
 *          2. 엑세스 토큰 정보 확인 API 구현
 *          3. 엑세스 토큰을 활용한 사용자 정보 확인 API 구현
 *
 *          **각 API 호출에 대한 NPE 처리 필요
 *          - 2022.08.23 박찬호
 */

@RequiredArgsConstructor
@Service
public class KakaoService {

    private final UserRepository userRepository;
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

    public ResponseEntity<?> kakaoLogin(String code)  {
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

        System.out.println("토큰: " + token.toString());

        //카카오 토큰 정보 보기 API 호출
        KakaoDto.TokenInfo tokenInfo = getTokenInfo(token.getAccess_token());

        System.out.println("**토큰정보: " + tokenInfo.toString());

        //기존 유저 → 200 Status + 닉네임
        Optional<User> user = userRepository.findByKakaoId(tokenInfo.getId());
        if (user.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(user.get().getNickname());
        }
        //신규 유저 → 201 Status + 토큰
        else {
            return ResponseEntity.status(HttpStatus.CREATED).body(token);
        }
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
    public KakaoDto.UserInfo getUserInfo(String token) {
        return webClient.get()
                .uri("https://kapi.kakao.com/v2/user/me")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(KakaoDto.UserInfo.class)
                .block();
    }
}