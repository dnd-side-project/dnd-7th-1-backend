package com.dnd.ground.domain.matrix.dto;

import com.dnd.ground.domain.matrix.MatrixUserCond;
import com.dnd.ground.global.exception.ExceptionCodeSet;
import com.dnd.ground.global.exception.ExerciseRecordException;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * @description 특정 영역을 조회하기 위한 Request DTO
 * @author  박찬호
 * @since   2023.03.12
 * @updated 1. 영역 조회 시, 일정 기간 조회를 위한 필드 추가
 *          - 2023.05.01 박찬호
 */
@Getter
public class MatrixRequestDto {
    public MatrixRequestDto(String nickname, MatrixUserCond type, Double latitude, Double longitude, String uuid, Double spanDelta,
                            LocalDateTime started, LocalDateTime ended) {
        if (latitude == null || longitude == null) throw new ExerciseRecordException(ExceptionCodeSet.MATRIX_CENTER_EMPTY);
        if (spanDelta == null) throw new ExerciseRecordException(ExceptionCodeSet.SPAN_DELTA_EMPTY);

        this.nickname = nickname;
        this.type = type;
        this.uuid = uuid;
        this.location = new Location(latitude, longitude);
        this.spanDelta = spanDelta;
        this.started = started;
        this.ended = ended;
    }
    @ApiModelProperty(value="닉네임", example="nickA")
    private String nickname;
    @ApiModelProperty(value="조회 타입(ALL|CHALLENGE)", example="ALL")
    private MatrixUserCond type;
    @ApiModelProperty(value="타입이 챌린지인 경우 해당 챌린지의 UUID", example="1938as9x8c9n1kd")
    private String uuid;
    @ApiModelProperty(value="조회할 영역의 location", example="{\"latitude\":32.123,\"longitude\":123.456}")
    private Location location;
    @ApiModelProperty(value="영역의 크기 spanDelta", example="0.003")
    private Double spanDelta;

    @ApiModelProperty(value="영역을 조회하려는 기간의 시작", example="2023-02-01T00:00:00")
    private LocalDateTime started;
    @ApiModelProperty(value="영역을 조회하려는 기간의 끝", example="2023-02-07T23:59:59")
    private LocalDateTime ended;

    public void setStarted(LocalDateTime started) {
        this.started = started;
    }

    public void setEnded(LocalDateTime ended) {
        this.ended = ended;
    }
}