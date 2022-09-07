package com.dnd.ground.domain.user.service;

import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.dto.JwtUserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @description 회원의 인증/인가 및 로그인 관련 서비스 인터페이스
 * @author  박세헌, 박찬호
 * @since   2022-09-07
 * @updated 1.회원 인증/인가 및 로그인 관련 메소드 이동(UserService -> AuthService)
 *          2.닉네임 유효성 검사 기능 구현
 *          2022-09-07 박찬호
 */
public interface AuthService {
    User save(JwtUserDto user);
    ResponseEntity<Map<String, String>> getNicknameByToken(HttpServletRequest request);
    UserDetails loadUserByUsername(String nickname);

    Boolean validateNickname(String nickname);
}
