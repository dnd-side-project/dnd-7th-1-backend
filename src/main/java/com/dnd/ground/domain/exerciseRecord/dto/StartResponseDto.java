package com.dnd.ground.domain.exerciseRecord.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @description 기록 시작시 Response Dto
 *              1. 운동기록 id
 *              2. (일주일)누적 칸의 수
 * @author  박세헌
 * @since   2022-08-02
 * @updated 2022-08-05 / 기록 api 명세 추가: 박세헌
 */

@Data
@AllArgsConstructor
public class StartResponseDto {

    @ApiModelProperty(value="운동기록 id", example="1", required = true)
    private Long recordId;

    @ApiModelProperty(value="누적 영역", example="20", required = true)
    private Integer areaNumber;
}
