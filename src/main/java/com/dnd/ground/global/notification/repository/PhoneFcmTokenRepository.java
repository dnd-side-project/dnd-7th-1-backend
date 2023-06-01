package com.dnd.ground.global.notification.repository;

import com.dnd.ground.global.notification.cache.PhoneFcmToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @description 핸드폰 타입의 FCM 토큰을 조회하기 위한 Repository
 * @author  박찬호
 * @since   2023-05-11
 * @updated 1. 클래스 생성
 *          -2023-05-11 박찬호
 */

@Repository
public interface PhoneFcmTokenRepository extends CrudRepository<PhoneFcmToken, String> {

}