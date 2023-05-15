package com.dnd.ground.global.auth.service;

import com.dnd.ground.global.auth.UserClaim;
import com.dnd.ground.global.auth.dto.FcmTokenUpdateDto;
import com.dnd.ground.global.auth.dto.UserSignDto;
import com.dnd.ground.global.exception.ExceptionCodeSet;
import com.dnd.ground.global.util.DeviceType;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletResponse;

/**
 * @description 회원의 인증/인가 및 회원 정보 관련 서비스 인터페이스
 * @author  박찬호
 * @since   2022-09-07
 * @updated 1. 로그아웃 API 구현
 *          - 2023.05.15 박찬호
 */
public interface AuthService {
    UserDetails loadUserByUsername(String nickname);
    Boolean validateNickname(String nickname);
    UserSignDto.Response  signUp(UserSignDto signDto, HttpServletResponse response);
    UserClaim getUserClaim(String token);
    ExceptionCodeSet updateFcmToken(FcmTokenUpdateDto request);
    ExceptionCodeSet logout(String nickname, DeviceType type);
}
