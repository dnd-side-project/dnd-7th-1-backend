package com.dnd.ground.domain.user.service;

import com.dnd.ground.domain.friend.FriendStatus;
import com.dnd.ground.domain.friend.service.FriendService;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.dto.KakaoDto;
import com.dnd.ground.domain.user.dto.SocialResponseDto;
import com.dnd.ground.domain.user.repository.UserRepository;
import com.dnd.ground.global.exception.AuthException;
import com.dnd.ground.global.exception.ExceptionCodeSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
 * @author 박찬호
 * @description 카카오를 비롯한 회원 정보와 관련한 서비스
 * @since 2022-08-23
 * @updated 1. 카카오 엑세스 토큰을 통한 회원정보 반환 API 생성
 *           - 2023.01.20 박찬호
 */

@RequiredArgsConstructor
@Slf4j
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

    /**
     * 카카오 토큰 발급
     */
    public SocialResponseDto.KakaoRedirectDto kakaoRedirect(String code) throws NullPointerException {
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

        SocialResponseDto.KakaoRedirectDto response = new SocialResponseDto.KakaoRedirectDto(token.getAccess_token(), token.getRefresh_token());

        //이메일 받아오기
        try {
            KakaoDto.UserInfo userInfo = getUserInfo(token.getAccess_token());
            response.setEmail(userInfo.getEmail());
        } catch (ParseException e) {
            throw new AuthException(ExceptionCodeSet.WEBCLIENT_ERROR);
        }

        return response;
    }

    public SocialResponseDto kakaoLogin(String token) throws ParseException {
        KakaoDto.UserInfo userInfo = getUserInfo(token);
        Optional<User> userOpt = userRepository.findByKakaoId(userInfo.getKakaoId());

        boolean isSigned;
        String email = userInfo.getEmail();
        String picturePath;
        String pictureName;

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            pictureName = user.getPictureName();
            picturePath= user.getPicturePath();
            isSigned = true;
        } else {
            picturePath = userInfo.getPicturePath();
            pictureName = userInfo.getPictureName();
            isSigned = false;
        }

        return new SocialResponseDto(email, picturePath, pictureName, isSigned);
    }

    /*카카오 엑세스 토큰 정보 확인*/
    public KakaoDto.TokenInfo getTokenInfo(String token) {
        return webClient.get()
                .uri("https://kapi.kakao.com/v1/user/access_token_info")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(KakaoDto.TokenInfo.class)
                .block();
    }

    /*사용자 정보 확인*/
    public KakaoDto.UserInfo getUserInfo(String token) throws ParseException {
        log.info("유저 확인 토큰:{}", token);
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
                .kakaoId(kakaoId)
                .email((String) jsonObject.get("email"))
                .pictureName("kakao/" + kakaoId)
                .picturePath((String) pictureObject.get("profile_image_url"))
                .build();
    }

    /*카카오 친구 목록 조회*/
    public KakaoDto.kakaoFriendResponse getKakaoFriends(String token, Integer offset) throws ParseException {
        WebClient webClient = WebClient.create();

        final int PAGING_NUMBER = 15;

        //회원 조회
        KakaoDto.UserInfo userInfo = getUserInfo(token);
        Long kakaoId = userInfo.getKakaoId();
        Optional<User> userOpt = userRepository.findByKakaoId(kakaoId);

        //Variable setting
        KakaoDto.kakaoFriendResponse response = new KakaoDto.kakaoFriendResponse();
        int nextOffset = 100;
        boolean isLast = false;

        //친구 조회
        KakaoDto.FriendsInfoFromKakao kakaoFriends = requestKakaoFriends(token, offset);
        List<KakaoDto.FriendsInfoFromKakao.KakaoFriend> elements = Objects.requireNonNull(kakaoFriends).getElements();

        //신규 유저
        if (userOpt.isEmpty()) {
            Loop1:
            while (response.getFriendsInfo().size() < PAGING_NUMBER) {
                for (KakaoDto.FriendsInfoFromKakao.KakaoFriend element : elements) {
                    Optional<User> kakaoUserInNemoduOpt = userRepository.findByKakaoId(element.getId());

                    //네모두 회원인 카카오 친구는 친구 추천에 포함
                    if (kakaoUserInNemoduOpt.isPresent()) {
                        User kakaoUserInNemodu = kakaoUserInNemoduOpt.get();
                        response.getFriendsInfo().add(
                                KakaoDto.kakaoFriendResponse.FriendsInfo.builder()
                                        .nickname(kakaoUserInNemodu.getNickname())
                                        .kakaoName(element.getProfile_nickname())
                                        .status(FriendStatus.NoFriend)
                                        .picturePath(kakaoUserInNemodu.getPicturePath())
                                        .build()
                        );
                    }

                    if (response.getFriendsInfo().size() >= PAGING_NUMBER || kakaoFriends.getAfter_url() == null) {
                        break Loop1;
                    }

                    nextOffset += 100;
                    kakaoFriends = requestKakaoFriends(token, nextOffset);
                }
                if (kakaoFriends.getAfter_url() == null) break;
            }
        } else {
            //기존 유저
            User user = userOpt.get();
            Loop1:
            while (response.getFriendsInfo().size() < PAGING_NUMBER) {
                for (KakaoDto.FriendsInfoFromKakao.KakaoFriend kakaoFriend : elements) {
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

                    if (response.getFriendsInfo().size() >= PAGING_NUMBER || kakaoFriends.getAfter_url() == null) {
                        break Loop1;
                    }

                    nextOffset += 100;
                    kakaoFriends = requestKakaoFriends(token, nextOffset);
                }
            }
        }

        //다음 URL이 없으면 마지막 페이지.
        if (kakaoFriends.getAfter_url() == null) {
            isLast = true;
            nextOffset = 0;
        }

        response.setNextOffset(nextOffset);
        response.setSize(response.getFriendsInfo().size());
        response.setIsLast(isLast);

        return response;
    }

    /*카카오 친구 목록 요청*/
    public KakaoDto.FriendsInfoFromKakao requestKakaoFriends(String token, int offset) {
        return webClient.get()
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
    }
}