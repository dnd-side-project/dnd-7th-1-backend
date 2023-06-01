package com.dnd.ground.global.auth.controller;

import com.dnd.ground.domain.user.LoginType;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.repository.UserPropertyRepository;
import com.dnd.ground.global.auth.dto.*;
import com.dnd.ground.global.auth.service.AppleService;
import com.dnd.ground.global.auth.service.AuthService;
import com.dnd.ground.global.auth.service.KakaoService;
import com.dnd.ground.global.exception.AuthException;
import com.dnd.ground.global.exception.ExceptionCodeSet;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @author 박찬호
 * @description 회원의 인증/인가 및 로그인 관련 컨트롤러
 * @since 2022-08-23
 * @updated 1. 카카오 친구 목록 조회 API 수정
 *          - 2023.05.25 박찬호
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
    private final UserPropertyRepository userPropertyRepository;

    @PostMapping("/sign")
    @Operation(summary = "회원 가입 V2", description = "변경된 회원가입")
    public ResponseEntity<UserSignDto.Response> signUp(@Valid @RequestBody UserSignDto request, HttpServletResponse response) {
        return ResponseEntity.ok().body(authService.signUp(request, response));
    }

    @GetMapping("/social/login")
    @Operation(summary = "소셜 로그인 이후 회원 정보를 받는 API", description = "헤더의 Authorization 에 각 로그인 타입에 맞는 토큰을 넣는다.\n카카오:카카오의 Access token\n애플:idToken\ntype:APPLE or KAKAO")
    public ResponseEntity<SocialResponseDto> socialLogin(@RequestHeader("Authorization") String token,
                                                         @RequestParam("type") LoginType type) {
        if (type.equals(LoginType.KAKAO)) return ResponseEntity.ok(kakaoService.kakaoLogin(token));
        else if (type.equals(LoginType.APPLE)) return ResponseEntity.ok(appleService.appleLogin(token));
        else throw new AuthException(ExceptionCodeSet.LOGIN_TYPE_INVALID);
    }

    @GetMapping("/check/nickname")
    @Operation(summary = "닉네임 유효성 검사", description = "2~6글자 || 중복X")
    public ResponseEntity<Boolean> validateNickname(@RequestParam("nickname") String nickname) {
        return ResponseEntity.ok(authService.validateNickname(nickname));
    }

    @PostMapping("/fcm/token")
    @Operation(summary = "FCM 토큰 추가", description = "FCM 토큰을 추가합니다.")
    public ResponseEntity<ExceptionCodeSet> updateFcmToken(@Valid @RequestBody FcmTokenUpdateDto request) {
        return ResponseEntity.ok().body(authService.updateFcmToken(request));
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "로그아웃 과정\n1.FCM 토큰 제거")
    public ResponseEntity<ExceptionCodeSet> logout(@Valid @RequestBody LogoutDto request) {
        return ResponseEntity.ok().body(authService.logout(request.getNickname(), request.getDeviceType()));
    }

    @DeleteMapping("/sign")
    @Operation(summary = "회원 탈퇴", description = "Resource Owner(소셜)의 종류와 상관 없이 호출해주시면 됩니다.")
    public ResponseEntity<ExceptionCodeSet> deleteUser(@RequestHeader(value = "Kakao-Access-Token", required = false) String kakaoToken,
                                                       @RequestParam("nickname") String nickname,
                                                       @RequestParam("loginType") LoginType loginType) {
        return ResponseEntity.ok().body(authService.deleteUser(nickname, kakaoToken, loginType));
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

    @GetMapping("/kakao/friend")
    @Operation(summary = "카카오 엑세스 토큰으로 카카오 친구 불러오기", description = "검수 전이라 팀 멤버들만 친구로 조회할 수 있음.")
    public ResponseEntity<KakaoDto.KakaoFriendResponse> getKakaoFriends(@RequestHeader("Kakao-Access-Token") String token,
                                                                        @RequestParam(value = "nickname", required = false) String nickname,
                                                                        @RequestParam("offset") Integer offset,
                                                                        @RequestParam("size") Integer size) {
        return ResponseEntity.ok(kakaoService.getKakaoFriends(nickname, token, offset, size));
    }

    @PostMapping("/kakao/message/{uuid}")
    @Operation(summary = "카카오 초대 메시지 전송", description = "사용자 정의 템플릿으로 초대 메시지 전송\n친구 목록에서 받은 UUID를 넘겨줘야 함.")
    public ResponseEntity<ExceptionCodeSet> sendInviteMessage(@RequestHeader("Kakao-Access-Token") String token,
                                                              @PathVariable("uuid") String uuid,
                                                              @RequestParam("nickname") String nickname) {
        return ResponseEntity.ok(kakaoService.sendInviteMessage(token, uuid, nickname));
    }

    @PostMapping("/kakao/delete")
    @Operation(summary = "카카오 회원 탈퇴 Callback API", description = "카카오 연결 끊기 이후, 서비스 탈퇴를 위한 콜백 API")
    public ResponseEntity<ExceptionCodeSet> deleteUser(@RequestBody KakaoDto.KakaoUnlinkCallbackDto request) {
        User user = userPropertyRepository.findUserBySocialId(String.valueOf(request.getKakao_id()));

        return ResponseEntity.ok().body(authService.deleteUser(user));
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