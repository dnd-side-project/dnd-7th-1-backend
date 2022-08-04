package com.dnd.ground.global.util;

import com.fasterxml.uuid.Generators;

import java.util.UUID;

/**
 * @description UUID 성능 개선을 위한 Sequential UUID 생성기
 * @author  박찬호
 * @since   2022-08-04
 * @updated 1. UUID Util 클래스 생성
 *          - 2022.08.04 박찬호
 */

public class UuidUtil {

    static public String createUUID() {
        //sequential uuid 생성
        UUID uuid = Generators.timeBasedGenerator().generate();

        String[] uuidArr = uuid.toString().split("-");
        String uuidStr = uuidArr[2]+uuidArr[1]+uuidArr[0]+uuidArr[3]+uuidArr[4];
        StringBuffer sb = new StringBuffer(uuidStr);

        return sb.toString();
    }
}