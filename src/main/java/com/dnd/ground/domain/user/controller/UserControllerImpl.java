package com.dnd.ground.domain.user.controller;

import com.dnd.ground.domain.user.dto.HomeResponseDto;
import com.dnd.ground.domain.user.service.UserService;
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
 * @updated 2022-08-04 / 홈화면 api : 박세헌
 */

@Api(tags = "유저")
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user")
@RestController
public class UserControllerImpl implements UserController {

    private final UserService userService;

    @GetMapping("/home")
    public ResponseEntity<HomeResponseDto> home(@RequestParam("nickname") String nickName){
        return ResponseEntity.ok(userService.showHome(nickName));
    }
}
