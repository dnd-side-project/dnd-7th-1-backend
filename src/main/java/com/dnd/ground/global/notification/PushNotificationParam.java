package com.dnd.ground.global.notification;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * @description 예약된 푸시 알람의 파라미터 (data 값)
 * @author  박찬호
 * @since   2023-04-22
 * @updated 1.예약 푸시 알람에 대해 Key-value 형태로 파라미터 변경에 유연하게 대처
 *          - 2023-05-13 박찬호
 */

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PushNotificationParam {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "params", nullable = false)
    private PushNotification notification;

    @Enumerated(EnumType.STRING)
    @Column(name = "param_key", nullable = false)
    private PushNotificationParamList key;

    @Column(name = "param_value", nullable = false)
    private String value;

    public PushNotificationParam(PushNotificationParamList key, String value) {
        this.key = key;
        this.value = value;
    }

    public void setPushNotification(PushNotification notification) {
        this.notification = notification;
    }
}
