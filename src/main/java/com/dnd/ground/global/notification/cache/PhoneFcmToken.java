package com.dnd.ground.global.notification.cache;

import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;

/**
 * @description 핸드폰에 대한 토큰 정보
 * @author  박찬호
 * @since   2023-05-11
 * @updated 1.캐시 정보 구현
 *          -2023-05-11 박찬호
 */

@RedisHash(value="fcm_phone", timeToLive = 5184000)
@Getter
public class PhoneFcmToken extends FcmToken {

    public PhoneFcmToken(String nickname, String fcmToken, Long ttl) {
        super(nickname, fcmToken, ttl);
    }
}