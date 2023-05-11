package com.dnd.ground.domain.user;

import com.dnd.ground.global.util.DeviceType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @description 디바이스 타입에 따라 저장될 FCM 토큰 엔티티
 *              레디스에서 만료되면 DB에서 토큰을 조회하고, 재발급을 요청한다.
 * @author  박찬호
 * @since   2023-05-11
 * @updated 1.엔티티 생성
 *          - 2023-05-11 박찬호
 */

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserPropertyFcmToken {
    @Id @GeneratedValue
    private Long id;

    private String nickname;

    @Enumerated(EnumType.STRING)
    private DeviceType type;

    private String fcmToken;

    @LastModifiedDate
    private LocalDateTime modified;

    public UserPropertyFcmToken(String nickname, DeviceType type, String token) {
        this.nickname = nickname;
        this.type = type;
        this.fcmToken = token;
        this.modified = LocalDateTime.now();
    }

    public void setFcmToken(String token) {
        this.fcmToken = token;
    }
}
