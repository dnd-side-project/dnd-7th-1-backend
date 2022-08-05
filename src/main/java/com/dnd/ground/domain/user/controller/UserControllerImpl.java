package com.dnd.ground.domain.user.controller;

import com.dnd.ground.domain.user.dto.HomeResponseDto;
import com.dnd.ground.domain.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
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
 * @updated 2022-08-05 / 홈화면 api 명세 추가 : 박세헌
 */

@Api(tags = "유저")
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user")
@RestController
public class UserControllerImpl implements UserController {

    private final UserService userService;

    @GetMapping("/home")
    @Operation(summary = "홈 화면 조회", description = "닉네임을 통해 홈화면에 필요한 유저, 친구, 챌린지 정보 조회")
    public ResponseEntity<HomeResponseDto> home(@RequestParam("nickname") String nickName){
        return ResponseEntity.ok(userService.showHome(nickName));
    }
}
