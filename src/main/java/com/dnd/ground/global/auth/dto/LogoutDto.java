package com.dnd.ground.global.auth.dto;

import com.dnd.ground.global.util.DeviceType;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @description 로그아웃 DTO
 * @author 박찬호
 * @since 2023-05-15
 * @updated 1.로그아웃 DTO 생성
 *          - 2023.05.15 박찬호
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class LogoutDto {
    @ApiModelProperty(value="닉네임", example = "user1", required = true)
    @NotNull
    private String nickname;

    @ApiModelProperty(value="디바이스 종류", example = "PAD", required = true)
    @NotNull
    private DeviceType type;
}
