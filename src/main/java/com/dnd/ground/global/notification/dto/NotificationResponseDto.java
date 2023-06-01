package com.dnd.ground.global.notification.dto;

import com.dnd.ground.global.notification.NotificationMessage;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * @description 푸시 알람 Response DTO
 * @author  박찬호
 * @since   2023-05-13
 * @updated  1.알람 조회할 때, 화면 전환을 위한 데이터 추가
 *          - 2023-05-31 박찬호
 */

@Getter
@AllArgsConstructor
public class NotificationResponseDto {
    @ApiModelProperty(value = "메시지 번호", example = "1234567")
    private String messageId;

    @ApiModelProperty(value = "메시지 제목", example = "USER1님이 챌린지에 초대했습니다.")
    private String title;

    @ApiModelProperty(value = "메시지 내용", example = "회원님의 친구 수락을 기다리고 있어요.")
    private String content;

    @ApiModelProperty(value = "읽었는지 여부", example = "true")
    private Boolean isRead;

    @ApiModelProperty(value = "메시지 종류", example = "COMMON")
    private NotificationMessage type;

    @ApiModelProperty(value = "발송 시간", example = "2023-05-10T22:50:22")
    private LocalDateTime reserved;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private NotificationChallengeData challengeData;

    public NotificationResponseDto(String messageId, String title, String content, Boolean isRead, NotificationMessage type, LocalDateTime reserved) {
        this.messageId = messageId;
        this.title = title;
        this.content = content;
        this.isRead = isRead;
        this.type = type;
        this.reserved = reserved;
    }

    public void setChallengeData(NotificationChallengeData challengeData) {
        this.challengeData = challengeData;
    }

    @AllArgsConstructor
    @Getter
    public static class NotificationChallengeData {
        private String challengeUuid;
    }
}
