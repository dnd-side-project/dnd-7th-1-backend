package com.dnd.ground.global.util;

import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;

/**

 * @description 요구사항에 따라 필요한 Util 파일
 *              - 회원이 탈퇴한 경우, (알 수 없음) 회원으로 끼워 넣어 챌린지 기록에서 확인할 수 있도록 하기 위해 DeleteUser 생성
 * @author 박찬호
 * @since 2023-05-22
 * @updated 1. 서버 구동할 때, DeleteUser 생성
 *          - 2023.05.22 박찬호
 */

@Component
@RequiredArgsConstructor
public class RequirementUtil {
    private final UserRepository userRepository;
    private static User deleteUser;
    private static final String DELETE_USER_NICKNAME = "(알 수 없음)";

    @Value("${picture.path}")
    private String DEFAULT_PATH;

    @Value("${picture.name}")
    private String DEFAULT_NAME;

    @PostConstruct
    public void init() {
        deleteUser = userRepository.findByNickname(DELETE_USER_NICKNAME)
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .nickname(DELETE_USER_NICKNAME)
                                .email("nemodu.official@gmail.com")
                                .created(LocalDateTime.now())
                                .intro("네모두를 탈퇴한 회원입니다.")
                                .pictureName(DEFAULT_NAME)
                                .picturePath(DEFAULT_PATH)
                                .build()));
    }

    public static User getDeleteUser() {
        return deleteUser;
    }
}
