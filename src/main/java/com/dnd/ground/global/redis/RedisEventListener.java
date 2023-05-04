package com.dnd.ground.global.redis;

import com.dnd.ground.domain.user.repository.UserRepository;
import com.dnd.ground.global.notification.NotificationService;
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
 * @updated 1.FCM 토큰 만료에 대한 이벤트 처리 구현
 *          - 2023-05-04 박찬호
 */

@Slf4j
@Component
public class RedisEventListener implements MessageListener {
    public RedisEventListener(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private final UserRepository userRepository;
    private static final Pattern fcmPattern = Pattern.compile("^fcm:.*$");

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String msg = message.toString();
        Matcher fcmMatcher = fcmPattern.matcher(msg);

        if (fcmMatcher.find()) {
            String nickname = fcmMatcher.group().split(":")[1];
            userRepository.findByNicknameWithProperty(nickname)
                   .ifPresentOrElse(NotificationService::requestReissueFCMToken, () -> log.warn("회원이 존재하지 않습니다."));
        }
    }
}
