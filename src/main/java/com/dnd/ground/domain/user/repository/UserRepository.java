package com.dnd.ground.domain.user.repository;

import com.dnd.ground.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @description 유저 리포지토리 인터페이스
 * @author  박세헌
 * @since   2022-08-01
 * @updated 2022-08-02 / 닉네임으로 유저 조회 : 박찬호
 */

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByNickName(String nickname);
}
