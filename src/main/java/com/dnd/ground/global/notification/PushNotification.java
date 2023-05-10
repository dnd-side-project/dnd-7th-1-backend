package com.dnd.ground.global.notification;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description 푸시 알람 기록용 엔티티
 * @author  박찬호
 * @since   2023-03-20
 * @updated 1.메시지에 포함된 파라미터 Map 반환 메소드 추가
 *          2.상태 변경 메소드 추가
 *          - 2023-05-05 박찬호
 */

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PushNotification {
    @Id
    private String messageId;

    @Column(name="title", nullable = false)
    private String title;

    @Column(name="content", nullable = false)
    private String content;

    @Column(name="created", nullable = false)
    private LocalDateTime created;

    @Column(name="reserved", nullable = false)
    private LocalDateTime reserved;

    @Column(name="status", nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    @Column(name="target_nickname", nullable = false)
    private String nickname;

    @OneToMany(mappedBy = "notification", cascade = CascadeType.ALL)
    private List<PushNotificationParam> params;

    public PushNotification(String messageId, String title, String content,
                            LocalDateTime created, LocalDateTime reserved, NotificationStatus status, String nickname) {
        this.messageId = messageId;
        this.title = title;
        this.content = content;
        this.created = created;
        this.reserved = reserved;
        this.status = status;
        this.nickname = nickname;
        this.params = new ArrayList<>();
    }

    public void setParams(List<PushNotificationParam> params) {
        this.params = params;
        for (PushNotificationParam param : params) {
            param.setPushNotification(this);
        }
    }

    public void updateStatus(NotificationStatus status) {
        this.status = status;
    }

    public Map<String, String> getParamMap() {
        Map<String, String> result = new HashMap<>();

        if (this.params == null) return new HashMap<>();
        else {
            for (PushNotificationParam param : this.params) {
                result.put(param.getKey().name().toLowerCase(), param.getValue());
            }
            return result;
        }
    }
}
