package com.dnd.ground.domain.user.repository;

import com.dnd.ground.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @description 유저 리포지토리 인터페이스
 * @author  박세헌
 * @since   2022-08-01
 * @updated 2022-08-12 / findMatrixCount함수 ExerciseRecord단으로 이동
 *                       - 박세헌
 */

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByNickname(String nickname);

}
