package com.dnd.ground.domain.user.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description 메인홈 구성 컨트롤러 클래스
 * @author  박세헌
 * @since   2022-08-02
 * @updated 2022-08-02 / 홈화면 구성 : 박세헌
 */

@Api(tags = "유저")
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user")
@RestController
public class UserControllerImpl implements UserController {

    @GetMapping("/home")
    public ResponseEntity<?> home(@RequestParam("nickName") String nickName){
        // 닉네임으로 유저 찾기
        // 유저의 친구, 챌린지 중인 친구 찾기
        // 정보들을 HomeResponseDto에 담아서 반환
        return ResponseEntity.ok("홈 화면");
    }
}
