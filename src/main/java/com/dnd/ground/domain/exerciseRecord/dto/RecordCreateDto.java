package com.dnd.ground.domain.exerciseRecord.dto;

import com.dnd.ground.domain.matrix.dto.Location;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * @description 기록 생성 DTO
 * @author  박찬호
 * @since   2022-08-02
 * @updated 1.구조 변경에 따른 클래스 이름 변경
 *          2023-03-05 박찬호
 */

@Data
public class RecordCreateDto {

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
            example = "[ { \"latitude\" : \"123.123\", \"longitude\" : \"37.123\" }, {\"latitude\" : \"123.123\",\"longitude\" : \"37.123\" } ]", dataType = "list", required = true)
    private ArrayList<Location> matrices;

}
