package com.dnd.ground.global.batch;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @description Batch Job Parameter Converter
 *              String으로 들어오는 LocalTime을 챌린지에 맞게 LocalDateTime으로 변환
 * @author  박찬호
 * @since   2023-04-15
 * @updated 1. 클래스 ㅅ구현
 *          - 2023.04.15 박찬호
 */


@Getter
@NoArgsConstructor
public class JobParamDateTimeConverter {
    private LocalDateTime created;

    public JobParamDateTimeConverter(String createdStr) {
        this.created = LocalDateTime.parse(createdStr).withHour(0).withMinute(0).withSecond(0).withNano(0);
    }
}
