package com.dnd.ground.global.auth.dto;

import com.dnd.ground.domain.user.LoginType;
import lombok.Getter;

import java.util.ArrayList;

/**
 * @description 회원가입 Request DTO
 * @author  박찬호
 * @since   2023.01.22
 * @updated 1. 소셜 로그인 후, 회원가입할 때 필요한 정보를 받아 회원 생성시 필요한 DTO
 *           - 2022-01-22 박찬호
 */
@Getter
public class UserSignDto {
    private Boolean isPublicRecord;
    private String email;
    private String nickname;
    private String picturePath;
    private String pictureName;
    private String intro;
    private LoginType loginType;
    private final ArrayList<String> friends = new ArrayList<>();
}
