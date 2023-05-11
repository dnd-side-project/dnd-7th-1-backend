package com.dnd.ground.global.auth.dto;

import com.dnd.ground.domain.user.LoginType;
import com.dnd.ground.global.util.DeviceType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;

/**
 * @description 회원가입 Request DTO
 * @author  박찬호
 * @since   2023.01.22
 * @updated 1. FCM 토큰 관리 정책 변경에 따른 DeviceType 추가
 *           - 2022-05-11 박찬호
 */
@Getter
public class UserSignDto {
    @NotNull
    private String email;

    @NotNull
    private String nickname;

    @NotNull
    private String picturePath;

    @NotNull
    private String pictureName;

    @NotNull
    private LoginType loginType;

    @NotNull
    private Boolean isPublicRecord;

    @NotNull
    private Boolean isNotification;

    @NotNull
    private String fcmToken;

    @NotNull
    private String socialId;

    @NotNull
    private DeviceType deviceType;

    @NotNull
    private final ArrayList<String> friends = new ArrayList<>();

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        private String nickname;
    }
}
