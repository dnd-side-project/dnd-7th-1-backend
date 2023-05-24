package com.dnd.ground.global.notification.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @description 알람 비우기 처리를 위한 Request DTO
 * @author  박찬호
 * @since   2023-05-24
 * @updated 1.클래스 생성
 *          2023-05-24 박찬호
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class NotificationDeleteRequestDto {
    @NotNull
    @ApiModelProperty(value = "Message ID 리스트", example = "[\"123\", \"456\"]")
    private List<String> notifications;
}
