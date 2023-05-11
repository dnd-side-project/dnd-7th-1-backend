package com.dnd.ground.global.notification.repository;

import com.dnd.ground.global.notification.cache.PadFcmToken;
import org.springframework.data.repository.CrudRepository;

/**
 * @description 패드 타입의 FCM 토큰을 조회하기 위한 Repository
 * @author  박찬호
 * @since   2023-05-11
 * @updated 1. 클래스 생성
 *          -2023-05-11 박찬호
 */

public interface PadFcmTokenRepository extends CrudRepository<PadFcmToken, String> {
}
