package com.dnd.ground.global.auth.service;

import com.dnd.ground.global.auth.UserClaim;
import com.dnd.ground.global.auth.dto.UserSignDto;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @description 회원의 인증/인가 및 회원 정보 관련 서비스 인터페이스
 * @author  박찬호
 * @since   2022-09-07
 * @updated 1. 미사용 API 삭제(온보딩, 회원가입V1)
 *          - 2023-01-23 박찬호
 */
public interface AuthService {
    UserDetails loadUserByUsername(String nickname);
    Boolean validateNickname(String nickname);
    UserClaim  signUp(UserSignDto signDto);
    UserClaim getUserClaim(String token);
}
