package com.dnd.ground.domain.user.controller;

import com.dnd.ground.domain.user.service.AuthService;
import com.dnd.ground.domain.user.service.KakaoService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @description 회원의 인증/인가 및 로그인 관련 컨트롤러
 * @author  박찬호
 * @since   2022-08-23
 * @updated 1.해당 컨트롤러에 대한 첫 URL Resource를 auth로 설정
 *          2.기존 유저인지 판별하는 API 추가
 *          - 2022.09.09 박찬호
 */

@Api(tags = "회원 인증/인가 및 로그인")
@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class AuthController {

    private final KakaoService kakaoService;
    private final AuthService authService;

    /*
    * 클라가 앱에 처음 진입했을때 액세스 토큰이 있다면 토큰과 함께 해당 uri로 호출
    * (JWTCheckFilter를 거친 후 닉네임 반환)
    * 토큰이 없다면 카카오 로그인 페이지로 가야함
    */
    @GetMapping("/main") // ** "/main" 에서 "/"으로 변경하는 건 어떨까??
    public ResponseEntity<?> onBoarding(HttpServletRequest request){
        return authService.getNicknameByToken(request);
    }

    @GetMapping("/check/origin")
    @Operation(summary = "카카오 엑세스 토큰으로 기존 유저인지 판별하기", description = "기존 유저:true \n신규 유저:false")
    public ResponseEntity<Boolean> isOriginalUser(HttpServletRequest request) {
        return ResponseEntity.ok(authService.isOriginalUser(request));
    }

    @GetMapping("/validate/nickname")
    @Operation(summary = "닉네임 유효성 검사", description = "2~6글자 || 중복X")
    public ResponseEntity<Boolean> validateNickname(@RequestParam("nickname") String nickname) {
        return ResponseEntity.ok(authService.validateNickname(nickname));
    }

    /**-- OAuth2.0 --**/
    @GetMapping("/kakao/login")
    @Operation(summary = "카카오 토큰 발급", description = "인가코드를 활용한 카카오 토큰 발급(엑세스, 리프레시)")
    public ResponseEntity<Map<String,String>> kakaoLogin(@RequestParam("code") String code) {
        return ResponseEntity.ok(kakaoService.kakaoLogin(code));
    }
}