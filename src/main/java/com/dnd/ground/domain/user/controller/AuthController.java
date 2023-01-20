package com.dnd.ground.domain.user.controller;

import com.dnd.ground.domain.user.LoginType;
import com.dnd.ground.domain.user.dto.*;
import com.dnd.ground.domain.user.service.AppleService;
import com.dnd.ground.domain.user.service.AuthService;
import com.dnd.ground.domain.user.service.KakaoService;
import com.dnd.ground.global.exception.AuthException;
import com.dnd.ground.global.exception.ExceptionCodeSet;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.UnknownHostException;

/**
 * @author 박찬호
 * @description 회원의 인증/인가 및 로그인 관련 컨트롤러
 * @since 2022-08-23
 * @updated 1.애플, 카카오 소셜 로그인에 대한 정보 반환 API 생성
 *          2.카카오 Redirect URI 변경
 *          - 2023.01.20 박찬호
 */

@Slf4j
@Api(tags = "회원 인증/인가, 로그인 및 OAuth(카카오)")
@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class AuthController {

    private final KakaoService kakaoService;
    private final AuthService authService;
    private final AppleService appleService;

    /*
     * 클라가 앱에 처음 진입했을때 액세스 토큰이 있다면 토큰과 함께 해당 uri로 호출
     * (JWTCheckFilter를 거친 후 닉네임 반환)
     * 토큰이 없다면 카카오 로그인 페이지로 가야함
     */
    @GetMapping("/")
    @Operation(summary = "자동 로그인", description = "앱에 처음 진입했을 때, 리프레시 토큰이 있으면 엑세스토큰과 함께 URI를 호출")
    public ResponseEntity<?> onBoarding(HttpServletRequest request) {
        return authService.getNicknameByToken(request);
    }

    @PostMapping("/signup")
    @Operation(summary = "회원 가입", description = "Request: 헤더에 Kakao-Access-Token:카카오 엑세스토큰, 바디에 닉네임, KakaoRefreshToken\nResponse: 헤더에 자체 엑세스, 리프레시 토큰 + 바디에 닉네임")
    public ResponseEntity<UserResponseDto.SignUp> signUp(@RequestHeader(value = "Kakao-Access-Token") String kakaoAccessToken,
                                                         @RequestBody UserRequestDto.SignUp request) throws ParseException, UnknownHostException {
        return authService.signUp(kakaoAccessToken, request);
    }

    @GetMapping("/check/nickname")
    @Operation(summary = "닉네임 유효성 검사", description = "2~6글자 || 중복X")
    public ResponseEntity<Boolean> validateNickname(@RequestParam("nickname") String nickname) {
        return ResponseEntity.ok(authService.validateNickname(nickname));
    }

    @GetMapping("/refreshToken")
    @Operation(summary = "네모두 토큰 재발급", description = "리프레시 토큰이 유효 하다면 토큰을 재발급")
    public ResponseEntity<Boolean> issuanceToken(@RequestHeader("Refresh-Token") String refreshToken) {
        return authService.issuanceToken(refreshToken);
    }

    /**
     * -- OAuth2.0 --
     **/
    @GetMapping("/kakao/redirect")
    @Operation(summary = "카카오 토큰 발급(Redirect URI)", description = "인가코드를 활용한 카카오 토큰 발급(엑세스, 리프레시)")
    public ResponseEntity<SocialResponseDto.KakaoRedirectDto> kakaoRedirect(@RequestParam("code") String code,
                                                          @RequestParam(value = "error", required = false) String error,
                                                          @RequestParam(value = "error_description", required = false) String error_description) {
        log.info("카카오 Redirect URI called: code: {} | error: {} | err_des: {}", code, error, error_description);
        return ResponseEntity.ok(kakaoService.kakaoRedirect(code));
    }

    @GetMapping("/social/login")
    @Operation(summary = "소셜 로그인 이후 회원 정보를 받는 API", description = "헤더의 Authorization 에 각 로그인 타입에 맞는 토큰을 넣는다.\n카카오:카카오의 Access token\n애플:idToken\ntype:APPLE or KAKAO")
    public ResponseEntity<SocialResponseDto> kakaoLogin(@RequestHeader("Authorization") String token, @RequestParam("type") LoginType type) throws ParseException {
        if (type.equals(LoginType.KAKAO)) return ResponseEntity.ok(kakaoService.kakaoLogin(token));
        else if (type.equals(LoginType.APPLE)) return ResponseEntity.ok(appleService.appleLogin(token));
        else throw new AuthException(ExceptionCodeSet.LOGIN_TYPE_INVALID);
    }

    @GetMapping("/check/origin")
    @Operation(summary = "카카오 엑세스 토큰으로 기존 유저인지 판별하기", description = "헤더에 카카오 엑세스토큰(키값:Access-Token)으로 보냄.\n기존 유저:true \n신규 유저:false")
    public ResponseEntity<Boolean> isOriginalUser(HttpServletRequest request) {
        return ResponseEntity.ok().body(authService.isOriginalUser(request));
    }

    @GetMapping("/kakao/friend")
    @Operation(summary = "카카오 엑세스 토큰으로 카카오 친구 불러오기", description = "검수 전이라 팀 멤버들만 친구로 조회할 수 있음.\n15명씩 페이징하도록 했음. 카카오 친구목록 조회의 경우 최초 offset=0, 이후 서버로부터 받은 offset으로 요청해야 함.")
    public ResponseEntity<KakaoDto.kakaoFriendResponse> getKakaoFriends(@RequestHeader("Kakao-Access-Token") String token,
                                                                        @RequestParam("offset") Integer offset) throws ParseException {
        return ResponseEntity.ok(kakaoService.getKakaoFriends(token, offset));
    }

    @PostMapping(value = "/apple/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> appleLoginRedirect(@RequestBody SocialResponseDto.AppleLoginResponseDto request) {
        String code = request.getCode();
        String idToken = request.getId_token();
        String user = request.getUser();
        String state = request.getState();
        log.info("code:{} | idToken:{} | user:{} | state:{}", code, idToken, user, state); //code, idToken STring임
        return null;
    }
}