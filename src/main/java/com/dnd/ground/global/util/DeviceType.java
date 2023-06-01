package com.dnd.ground.global.util;

import com.dnd.ground.global.exception.CommonException;
import com.dnd.ground.global.exception.ExceptionCodeSet;

/**
 * @description FCM 토큰 관리 전략 변경에 따른 디바이스 타입
 * @author  박찬호
 * @since   2023-05-11
 * @updated 1. String -> DeviceType 변경 메소드 생성
 *          -2023-05-11 박찬호
 */


public enum DeviceType {
    PHONE,
    PAD;

    public static String getRedisKey(DeviceType type) {
        if (type == PHONE) {
            return "fcm_phone";
        } else if (type == PAD) {
            return "fcm_pad";
        } else {
            throw new CommonException(ExceptionCodeSet.DEVICE_TYPE_INVALID);
        }
    }

    public static DeviceType getType(String type) {
        for (DeviceType t : values()) {
            if (t.name().equalsIgnoreCase(type)) return t;
        }
        throw new CommonException(ExceptionCodeSet.DEVICE_TYPE_INVALID);
    }
}
