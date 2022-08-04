package com.dnd.ground.domain.user.service;

import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.dto.HomeResponseDto;

/**
 * @description 유저 서비스 인터페이스
 * @author  박세헌, 박찬호
 * @since   2022-08-01
 * @updated 2022-08-04 / 홈 화면 구성 로직: 박세헌
 */

public interface UserService {
    User save(User user);
    HomeResponseDto showHome(String nickname);
}
