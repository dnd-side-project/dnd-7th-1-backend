package com.dnd.ground.global.util;

import com.dnd.ground.global.exception.CommonException;
import com.dnd.ground.global.exception.ExceptionCodeSet;
import com.fasterxml.uuid.Generators;

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * @description UUID 성능 개선을 위한 Sequential UUID 생성기
 * @author  박찬호
 * @since   2022-08-04
 * @updated 1.hexToBytes() 올바르지 않은 문자열에 대한 예외 처리 추가
 *          - 2023.05.30 박찬호
 */

public class UuidUtil {
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    static public byte[] createUUID() {
        //sequential newUuid 생성
        UUID uuidV1 = Generators.timeBasedGenerator().generate();
        String[] uuidV1Parts = uuidV1.toString().split("-");
        String sequentialUUID = uuidV1Parts[2]+uuidV1Parts[1]+uuidV1Parts[0]+uuidV1Parts[3]+uuidV1Parts[4];

        String sequentialUuidV1 = String.join("", sequentialUUID);
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(Long.parseUnsignedLong(sequentialUuidV1.substring(0, 16), 16));
        bb.putLong(Long.parseUnsignedLong(sequentialUuidV1.substring(16), 16));
        return bb.array();
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hexChars[i * 2] = HEX_ARRAY[v >>> 4];
            hexChars[i * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars).toLowerCase();
    }

    public static byte[] hexToBytes(String uuid) {
        String sequentialUuidV1 = String.join("", uuid);

        if (sequentialUuidV1.length() < 16) throw new CommonException(ExceptionCodeSet.UUID_INVALID);

        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(Long.parseUnsignedLong(sequentialUuidV1.substring(0, 16), 16));
        bb.putLong(Long.parseUnsignedLong(sequentialUuidV1.substring(16), 16));
        return bb.array();
    }
}