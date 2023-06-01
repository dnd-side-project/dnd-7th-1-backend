package com.dnd.ground.global.redis;

import com.dnd.ground.global.notification.NotificationService;
import com.dnd.ground.global.notification.repository.FcmTokenRepository;
import com.dnd.ground.global.util.DeviceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @description Redis Expire Event Listener
 * @author  박찬호
 * @since   2023-05-04
 * @updated 1.재발급 요청 방식 변경
 *          - 2023-05-11 박찬호
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisEventListener implements MessageListener {
    private final FcmTokenRepository fcmTokenRepository;
    private static final Pattern fcmPattern = Pattern.compile("^fcm.*$");

    @Override
    public void onMessage(Message message, byte[] pattern) {
        Matcher fcmMatcher = fcmPattern.matcher(message.toString());

        if (fcmMatcher.find()) {
            String[] keyAndValue = fcmMatcher.group().split(":");

            if (keyAndValue.length != 2) {
                log.warn("FCM 토큰 만료 이벤트의 메시지가 올바르지 않습니다: {}", message);
                return;
            }

            String nickname = keyAndValue[1];
            DeviceType type = DeviceType.getType(keyAndValue[0].split("_")[1]);

            String token = fcmTokenRepository.findToken(nickname, type);
            NotificationService.requestReissueFCMToken(nickname, token);
        }
    }
}
