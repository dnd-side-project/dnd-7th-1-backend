package com.dnd.ground.domain.exerciseRecord.dto;

import com.dnd.ground.domain.matrix.dto.MatrixDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @description 기록 끝 Request Dto
 *              1. 운동기록 id
 *              2. matrix 정보
 *              3. 거리
 * @author  박세헌
 * @since   2022-08-02
 * @updated 2022-08-17 / api 명세 수정 - 박세헌
 */

@Data
public class EndRequestDto {

    @ApiModelProperty(value="시작할때 넘겨준 운동기록 id", example="1", required = true)
    private Long recordId;

    @ApiModelProperty(value="거리", example="100", required = true)
    private Integer distance;

    @ApiModelProperty(value="운동시간(초)", example="80", required = true)
    private Integer exerciseTime;

    @ApiModelProperty(value="걸음수", example="100", required = true)
    private Integer stepCount;

    @ApiModelProperty(value="상세기록", example="재밌었다!", required = true)
    private String message;

    @ApiModelProperty(value="칸 꼭지점 위도, 경도 리스트", required = true)
    private List<MatrixDto> matrices = new ArrayList<>();

}
