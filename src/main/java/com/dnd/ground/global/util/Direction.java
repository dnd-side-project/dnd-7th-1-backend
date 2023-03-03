package com.dnd.ground.global.util;

import lombok.Getter;

/**
 * @description MBR 계산을 위한 방향 정보
 * @author 박찬호
 * @since 2023-02-18
 * @updated 1. enum 클래스 생성
 *          - 2023.02.18 박찬호
 */
@Getter
public enum Direction {
    NORTH(0.0),
    WEST(270.0),
    SOUTH(180.0),
    EAST(90.0),
    NORTHWEST(315.0),
    SOUTHWEST(225.0),
    SOUTHEAST(135.0),
    NORTHEAST(45.0);

    private final Double bearing;

    Direction(Double bearing) {
        this.bearing = bearing;
    }
}
