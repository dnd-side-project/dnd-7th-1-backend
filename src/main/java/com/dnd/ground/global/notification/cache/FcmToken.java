package com.dnd.ground.global.notification.cache;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.TimeToLive;

import java.util.concurrent.TimeUnit;

/**
 * @description FCM 토큰 캐싱 객체
 *              2달 간 유지되고, 삭제되면서 Event Publish.
 *              Device Type에 따라 저장되어야 하므로 추상 클래스를 통해 중복 코드 최소화
 * @author  박찬호
 * @since   2023-05-11
 * @updated 1.캐시 정보 구현
 *          -2023-05-11 박찬호
 */

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public abstract class FcmToken {
    @Id
    private String nickname;

    private String fcmToken;

    @TimeToLive(unit = TimeUnit.DAYS)
    private Long ttl;
}
