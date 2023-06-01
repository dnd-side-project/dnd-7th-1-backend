package com.dnd.ground.global.batch;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @description Batch Job Parameter Converter
 *              String으로 들어오는 LocalTime을 챌린지에 맞게 LocalDateTime으로 변환
 * @author  박찬호
 * @since   2023-04-15
 * @updated 1. 파싱 코드 수정
 *          - 2023.05.12 박찬호
 */


@Getter
@NoArgsConstructor
public class JobParamDateTimeConverter {
    private LocalDateTime created;

    public JobParamDateTimeConverter(String createdStr) {
        this.created = LocalDateTime.of(LocalDate.parse(createdStr), LocalTime.MIN);
    }
}
