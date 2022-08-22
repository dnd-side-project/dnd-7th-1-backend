package com.dnd.ground.domain.exerciseRecord.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * @description 기록 끝 Request Dto
 * @author  박세헌
 * @since   2022-08-02
 * @updated 2022-08-22 / 기록 시작-끝 필드 추가 - 박세헌
 */

@Data
public class EndRequestDto {

    @ApiModelProperty(name = "유저의 닉네임", example = "NickA")
    private String nickname;

    @ApiModelProperty(value="거리", example="100", required = true)
    private Integer distance;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    @ApiModelProperty(value="기록 시작 시간", example="2022-08-15T00:00:00", required = true)
    private LocalDateTime started;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    @ApiModelProperty(value="기록 끝 시간", example="2022-08-15T00:00:00", required = true)
    private LocalDateTime ended;

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
