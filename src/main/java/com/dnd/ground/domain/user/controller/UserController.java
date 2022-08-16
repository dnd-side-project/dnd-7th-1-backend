package com.dnd.ground.domain.user.controller;

import com.dnd.ground.domain.user.dto.UserResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @description 회원 관련 역할 분리 인터페이스
 * @author  박세헌, 박찬호
 * @since   2022-08-02
 * @updated 1. 랭킹 관련 메소드 이동(UserService -> MatrixService)
 *          - 2022.08.11 박찬호
 */

public interface UserController {
    ResponseEntity<?> home(@RequestParam("nickName") String nickName);
    ResponseEntity<UserResponseDto.UInfo> getUserInfo(@RequestParam("nickname") String nickname);
}
