package com.dnd.ground.global.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import org.springframework.data.annotation.Id;
import java.util.concurrent.TimeUnit;

/**
 * @description FCM 토큰 캐싱 객체
 *              2달 간 유지되고, 삭제되면서 Event Publish.
 *              캐시되지 않으면, DB에 저장된 값을 갖고 오고 재발급을 요청한다.
 * @author  박찬호
 * @since   2023-05-04
 * @updated 1.캐시 정보 구현
 *          -2023-05-04 박찬호
 */

@RedisHash(value="fcm", timeToLive = 5184000)
@Getter
@AllArgsConstructor
public class FCMCache {
    @Id
    private String nickname;

    private String fcmToken;

    @TimeToLive(unit = TimeUnit.DAYS)
    private Long ttl;
}