package com.dnd.ground.domain.user.repository;

import com.dnd.ground.domain.user.UserPropertyFcmToken;
import com.dnd.ground.global.util.DeviceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * @description FCM 토큰을 조회하기 위한 Repository
 * @author  박찬호
 * @since   2023-05-11
 * @updated 1.인터페이스 생성
 *          - 2023-05-11 박찬호
 */

public interface UserPropertyFcmTokenRepository extends JpaRepository<UserPropertyFcmToken, Long> {

    @Query("SELECT upf FROM UserPropertyFcmToken upf WHERE upf.nickname = :nickname AND upf.type = :type")
    Optional<UserPropertyFcmToken> findToken(@Param("nickname") String nickname, @Param("type") DeviceType type);

    @Transactional
    void deleteByNicknameAndType(@Param("nickname") String nickname, @Param("type") DeviceType type);

    @Transactional
    void deleteByNickname(@Param("nickname") String nickname);
}
