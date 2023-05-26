package com.dnd.ground.common;

import com.dnd.ground.domain.user.LoginType;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.UserProperty;
import com.dnd.ground.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class DataProvider {

    @Autowired
    UserRepository userRepository;

    @Value("${picture.path}")
    private String DEFAULT_PATH;

    @Value("${picture.name}")
    private String DEFAULT_NAME;

    public void createUser(int size) {
        for (int i = 1; i <= size; i++) {
            User user = User.builder()
                    .nickname("nick" + i)
                    .email("email" + i + "@gmail.com")
                    .intro("nick" + i + "의 소개 메시지")
                    .created(LocalDateTime.now())
                    .pictureName(DEFAULT_NAME)
                    .picturePath(DEFAULT_PATH)
                    .loginType(i % 2 == 0 ? LoginType.APPLE : LoginType.KAKAO)
                    .build();

            UserProperty property = UserProperty.builder()
                    .socialId(i % 2 == 0 ? null : String.valueOf(i))
                    .isExceptRecommend(false)
                    .isShowMine(true)
                    .isShowFriend(true)
                    .isPublicRecord(true)
                    .notiWeekStart(true)
                    .notiWeekEnd(true)
                    .notiFriendRequest(true)
                    .notiFriendAccept(true)
                    .notiChallengeRequest(true)
                    .notiChallengeAccept(true)
                    .notiChallengeStart(true)
                    .notiChallengeCancel(true)
                    .notiChallengeResult(true)
                    .build();

            user.setUserProperty(property);
            userRepository.save(user);
        }
    }
}
