package com.dnd.ground.domain.exerciseRecord.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @description 운동 기록 조회 관련 (QueryDSL) DTO
 * @author  박찬호
 * @since   2023-03-01
 * @updated 1. 클래스 생성
 *          - 2023-03-01 박찬호
 */
@Getter
@NoArgsConstructor
public class RecordDto {
    private Integer distance;
    private Integer exerciseTime;
    private Integer stepCount;
    private LocalDateTime started;
    private LocalDateTime ended;
    private String message;

    @QueryProjection
    public RecordDto(Integer distance, Integer exerciseTime, Integer stepCount, LocalDateTime started, LocalDateTime ended, String message) {
        this.distance = distance;
        this.exerciseTime = exerciseTime;
        this.stepCount = stepCount;
        this.started = started;
        this.ended = ended;
        this.message = message;
    }

    @Getter
    public static class Stats {
        private long stepCount;
        private long distanceCount;
        private long exerciseTimeCount;

        @QueryProjection
        public Stats(long stepCount, long distanceCount, long exerciseTimeCount) {
            this.stepCount = stepCount;
            this.distanceCount = distanceCount;
            this.exerciseTimeCount = exerciseTimeCount;
        }
    }
}
