package com.dnd.ground.domain.user.repository;

import com.dnd.ground.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @description 유저 리포지토리 인터페이스
 * @author  박세헌
 * @since   2022-08-01
 * @updated 2022-08-01 / 생성 : 박세헌
 */

public interface UserRepository extends JpaRepository<User, Long> {
}
