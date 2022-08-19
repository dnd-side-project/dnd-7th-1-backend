package com.dnd.ground.domain.exerciseRecord.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;

/**
 * @description 기록 끝 Request Dto
 * @author  박세헌
 * @since   2022-08-02
 * @updated 2022-08-19 / 칸 정보 이차배열로 수정 - 박세헌
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

    @ApiModelProperty(value="칸 꼭지점 위도, 경도 리스트",
            example = "[[37.123123, 127.123123], [37.234234, 127.234234]]", dataType = "list", required = true)
    private ArrayList<ArrayList<Double>> matrices;

}
