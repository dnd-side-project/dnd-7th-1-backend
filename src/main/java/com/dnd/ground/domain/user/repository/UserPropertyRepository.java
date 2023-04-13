package com.dnd.ground.domain.user.repository;

import com.dnd.ground.domain.user.UserProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * @description 회원 정보 레포지토리
 * @author  박찬호
 * @since   2023.03.20
 * @updated 1.User - UserProperty 분리에 따른 JPA 레포지토리 생성
 *          2.회원의 닉네임을 통해 회원 정보 조회 쿼리 생성
 *           - 2023-03-20 박찬호
 */

public interface UserPropertyRepository extends JpaRepository<UserProperty, Long> {
    @Query("SELECT up FROM UserProperty up INNER JOIN User u ON u.property = up AND u.nickname = :nickname")
    Optional<UserProperty> findByNickname(@Param("nickname") String nickname);
}
