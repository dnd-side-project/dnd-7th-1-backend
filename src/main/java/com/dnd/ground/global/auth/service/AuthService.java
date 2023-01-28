package com.dnd.ground.global.auth.service;

import com.dnd.ground.global.auth.UserClaim;
import com.dnd.ground.domain.user.dto.UserResponseDto;
import com.dnd.ground.global.auth.dto.UserSignDto;
import com.dnd.ground.global.auth.dto.JWTReissueResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;

/**
 * @description 회원의 인증/인가 및 회원 정보 관련 서비스 인터페이스
 * @author  박세헌, 박찬호
 * @since   2022-09-07
 * @updated 1. 미사용 API 삭제(온보딩, 회원가입V1)
 *          - 2023-01-23 박찬호
 */
public interface AuthService {
    UserDetails loadUserByUsername(String nickname);
    Boolean validateNickname(String nickname);

    Boolean isOriginalUser(HttpServletRequest request);

    UserClaim  signUp(UserSignDto signDto);

    ResponseEntity<JWTReissueResponseDto> issuanceToken(String refreshToken);

    ResponseEntity<UserResponseDto.UInfo> issuanceTokenByNickname(String nickname);
    UserClaim getUserClaim(String token);
}
