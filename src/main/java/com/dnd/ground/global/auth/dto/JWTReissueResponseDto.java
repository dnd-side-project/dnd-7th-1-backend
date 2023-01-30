package com.dnd.ground.global.auth.dto;

import com.dnd.ground.domain.user.LoginType;
import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * @description 토큰 재발급 후 응답 포맷
 * @author  박찬호
 * @since   2023.01.20
 * @updated 1. idToken 검증 및 회원 정보 반환 API 생성
 *           - 2022-01-20 박찬호
 */
@Getter
@AllArgsConstructor
public class JWTReissueResponseDto {
    private String msg;
    private String code;
    private LoginType type;
}
