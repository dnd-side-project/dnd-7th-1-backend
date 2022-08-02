package com.dnd.ground.domain.exerciseRecord.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @description 기록 시작시 Response Dto
 *              1. 운동기록 id
 *              2. (일주일)누적 칸의 수
 * @author  박세헌
 * @since   2022-08-02
 * @updated 2022-08-02 / 생성 : 박세헌
 */

@Data
@AllArgsConstructor
public class StartResponseDto {
    private Long recordId;
    private Integer matrixNumber;
}
