package com.dnd.ground.domain.user.controller;

import com.dnd.ground.domain.user.service.AuthService;
import com.dnd.ground.domain.user.service.KakaoService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @description 회원의 인증/인가 및 로그인 관련 컨트롤러
 * @author  박찬호
 * @since   2022-08-23
 * @updated 1. 닉네임 유효성 검사 기능 구현
 *          - 2022.09.07 박찬호
 */

@Api(tags = "회원 인증/인가 및 로그인")
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final KakaoService kakaoService;
    private final AuthService authService;

    /*
    * 클라가 앱에 처음 진입했을때 액세스 토큰이 있다면 토큰과 함께 해당 uri로 호출
    * (JWTCheckFilter를 거친 후 닉네임 반환)
    * 토큰이 없다면 카카오 로그인 페이지로 가야함
    */
    @GetMapping("/main")
    public ResponseEntity<?> onBoarding(HttpServletRequest request){
        return authService.getNicknameByToken(request);
    }


    @GetMapping("/validate/nickname")
    @Operation(summary = "닉네임 유효성 검사", description = "2~6글자 || 중복X")
    public ResponseEntity<Boolean> validateNickname(@RequestParam("nickname") String nickname) {
        return ResponseEntity.ok(authService.validateNickname(nickname));
    }

    /**-- OAuth2.0 --**/
    @GetMapping("/auth/kakao/login")
    @Operation(summary = "카카오 토큰 발급", description = "인가코드를 활용한 카카오 토큰 발급(엑세스, 리프레시)x")
    public ResponseEntity<?> kakaoLogin(@RequestParam("code") String code) {
        return kakaoService.kakaoLogin(code);
    }


}
