package com.dnd.ground.domain.exerciseRecord.dto;

import com.dnd.ground.domain.matrix.dto.MatrixDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @description 기록 끝 Request Dto
 *              1. 운동기록 id
 *              2. matrix 정보
 *              3. 거리
 * @author  박세헌
 * @since   2022-08-02
 * @updated 2022-08-09 / MatrixDto 변경: 박세헌
 */

@Data
public class EndRequestDto {

    @ApiModelProperty(value="운동기록 id", example="시작할때 넘겨준 운동기록 id", required = true)
    private Long recordId;

    @ApiModelProperty(value="거리", example="100", required = true)
    private Double distance;

    @ApiModelProperty(value="칸 꼭지점 위도, 경도 리스트", required = true)
    private List<MatrixDto> matrices = new ArrayList<>();

}
