package com.dnd.ground.global.auth.dto;

import com.dnd.ground.global.util.DeviceType;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotNull;

/**
 * @description 토큰 추가할 때 사용하는 DTo
 * @author 박찬호
 * @since 2023-05-15
 * @updated 1. DTO 구현
 *          - 2023.05.11 박찬호
 */

@Getter
@AllArgsConstructor
public class FcmTokenUpdateDto {
    @ApiModelProperty(value="닉네임", example = "user1", required = true)
    @NotNull
    private String nickname;

    @ApiModelProperty(value="새로운 FCM 토큰", example = "skvnaoxic92a", required = true)
    @NotNull
    private String fcmToken;

    @ApiModelProperty(value="장치 종류(폰, 패드)", example = "PHONE", required = true)
    @NotNull
    private DeviceType deviceType;
}
