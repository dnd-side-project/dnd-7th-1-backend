package com.dnd.ground.domain.exerciseRecord.dto;

import com.dnd.ground.domain.matrix.dto.MatrixDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @description 기록 끝 Request Dto
 * @author  박세헌
 * @since   2022-08-02
 * @updated 2022-08-18 / recordId 삭제, nickname 추가 - 박세헌
 */

@Data
public class EndRequestDto {

    @ApiModelProperty(name = "유저의 닉네임", example = "NickA")
    private String nickname;

    @ApiModelProperty(value="거리", example="100", required = true)
    private Integer distance;

    @ApiModelProperty(value="운동시간(초)", example="80", required = true)
    private Integer exerciseTime;

    @ApiModelProperty(value="걸음수", example="100", required = true)
    private Integer stepCount;

    @ApiModelProperty(value="상세 기록", example="상세 기록 예시", required = true)
    private String message;

    @ApiModelProperty(value="칸 꼭지점 위도, 경도 리스트", required = true)
    private List<MatrixDto> matrices = new ArrayList<>();

}
