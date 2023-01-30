package com.dnd.ground.global.auth.dto;

import com.dnd.ground.domain.user.LoginType;
import lombok.Getter;

/**
 * @description 로그인 Request DTO
 * @author  박찬호
 * @since   2023.01.29
 * @updated 1. 기존 회원이 소셜 로그인을 한 뒤, 토큰을 발급받기 위한 DTO
 *           - 2022-01-29 박찬호
 */
@Getter
public class UserLoginDto {
    private String email;
    private LoginType loginType;
}
