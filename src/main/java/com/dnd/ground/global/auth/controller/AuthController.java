package com.dnd.ground.global.auth.controller;

import com.dnd.ground.domain.user.LoginType;
import com.dnd.ground.domain.user.dto.*;
import com.dnd.ground.global.auth.dto.FcmTokenUpdateDto;
import com.dnd.ground.global.auth.dto.SocialResponseDto;
import com.dnd.ground.global.auth.service.AppleService;
import com.dnd.ground.global.auth.service.AuthService;
import com.dnd.ground.global.auth.service.KakaoService;
import com.dnd.ground.global.auth.dto.UserSignDto;
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

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @author 박찬호
 * @description 회원의 인증/인가 및 로그인 관련 컨트롤러
 * @since 2022-08-23
 * @updated 1. 회원가입 API @Valid 추가
 *          - 2023.05.11 박찬호
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

    @PostMapping("/sign")
    @Operation(summary = "회원 가입 V2", description = "변경된 회원가입")
    public ResponseEntity<UserSignDto.Response> signUp(@Valid @RequestBody UserSignDto request, HttpServletResponse response) {
        return ResponseEntity.ok().body(authService.signUp(request, response));
    }

    @GetMapping("/check/nickname")
    @Operation(summary = "닉네임 유효성 검사", description = "2~6글자 || 중복X")
    public ResponseEntity<Boolean> validateNickname(@RequestParam("nickname") String nickname) {
        return ResponseEntity.ok(authService.validateNickname(nickname));
    }

    @PostMapping("/fcm/token")
    @Operation(summary = "FCM 토큰 추가", description = "FCM 토큰을 추가합니다.")
    public ResponseEntity<ExceptionCodeSet> updateFcmToken(@RequestBody @Valid FcmTokenUpdateDto request) {
        return ResponseEntity.ok().body(authService.updateFcmToken(request));
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
    public ResponseEntity<SocialResponseDto> socialLogin(@RequestHeader("Authorization") String token, @RequestParam("type") LoginType type) {
        if (type.equals(LoginType.KAKAO)) return ResponseEntity.ok(kakaoService.kakaoLogin(token));
        else if (type.equals(LoginType.APPLE)) return ResponseEntity.ok(appleService.appleLogin(token));
        else throw new AuthException(ExceptionCodeSet.LOGIN_TYPE_INVALID);
    }

    /**
     * 카카오 토큰만 들어올 떄 필터 어떻게 할 지 생각해봐야함.
     * @param token
     * @param offset
     * @return
     * @throws ParseException
     */
    @GetMapping("/kakao/friend")
    @Operation(summary = "카카오 엑세스 토큰으로 카카오 친구 불러오기", description = "검수 전이라 팀 멤버들만 친구로 조회할 수 있음.\n15명씩 페이징하도록 했음. 카카오 친구목록 조회의 경우 최초 offset=0, 이후 서버로부터 받은 offset으로 요청해야 함.")
    public ResponseEntity<KakaoDto.kakaoFriendResponse> getKakaoFriends(@RequestHeader("Kakao-Access-Token") String token,
                                                                        @RequestParam("offset") Integer offset) throws ParseException {
        return ResponseEntity.ok(kakaoService.getKakaoFriends(token, offset));
    }

    @PostMapping(value = "/apple/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @Operation(summary = "애플 로그인 Redirect URI", description = "백엔드 테스트용")
    public ResponseEntity<?> appleLoginRedirect(@RequestBody SocialResponseDto.AppleLoginResponseDto request) {
        String code = request.getCode();
        String idToken = request.getId_token();
        String user = request.getUser();
        String state = request.getState();
        log.info("code:{} | idToken:{} | user:{} | state:{}", code, idToken, user, state);
        return null;
    }
}