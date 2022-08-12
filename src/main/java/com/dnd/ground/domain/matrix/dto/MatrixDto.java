package com.dnd.ground.domain.matrix.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @description 중복 있는 칸에 대한 정보 dto
 * @author  박찬호, 박세헌
 * @since   2022-08-05
 * @updated 2022-08-09 / 생성: 박세헌
 */

@Data @AllArgsConstructor
public class MatrixDto{
    @ApiModelProperty(value = "위도", example = "37.123123")
    private Double latitude;
    @ApiModelProperty(value = "위도", example = "127.123123")
    private Double longitude;
}