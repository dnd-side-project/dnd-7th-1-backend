package com.dnd.ground.global.auth.dto;

import com.dnd.ground.domain.user.LoginType;
import lombok.Getter;

import java.util.ArrayList;

/**
 * @description 회원가입 Request DTO
 * @author  박찬호
 * @since   2023.01.22
 * @updated 1. 회원가입 intro 삭제
 *           - 2022-01-31 박찬호
 */
@Getter
public class UserSignDto {
    private Boolean isPublicRecord;
    private String email;
    private String nickname;
    private String picturePath;
    private String pictureName;
    private LoginType loginType;
    private final ArrayList<String> friends = new ArrayList<>();
}
