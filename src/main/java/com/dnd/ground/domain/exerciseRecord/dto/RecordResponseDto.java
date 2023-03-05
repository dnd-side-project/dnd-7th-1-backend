package com.dnd.ground.domain.exerciseRecord.dto;

import com.dnd.ground.domain.challenge.dto.ChallengeResponseDto;
import com.dnd.ground.domain.matrix.dto.Location;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @description 운동기록 Response Dto
 *              1. 운동 기록 전체적인 정보 Dto
 *              2. 활동 기록 response Dto
 * @author  박세헌, 박찬호
 * @since   2022-08-16
 * @updated 1. MatrixDto -> Location 변경
 *          - 2023-03-05 박찬호
 */

@Data
public class RecordResponseDto {

    @Data @Builder
    static public class EInfo {
        @ApiModelProperty(value = "운동기록 id", example = "1")
        private Long recordId;

        @ApiModelProperty(value = "해당 기록의 날짜", example = "07월 25일 토요일")
        private String date;

        @ApiModelProperty(value="운동 기록 시작 시간", example="18:04")
        private String started;

        @ApiModelProperty(value="운동 기록 끝 시간", example="18:07")
        private String ended;

        @ApiModelProperty(value="해당 기록의 채운 칸의 수", example="9")
        private Long matrixNumber;

        @ApiModelProperty(value="해당 기록의 걸음의 수", example="300")
        private Integer stepCount;

        @ApiModelProperty(value="해당 기록의 거리", example="20")
        private Integer distance;

        @ApiModelProperty(value="해당 기록의 운동 시간", example="3:00")
        private String exerciseTime;

        @ApiModelProperty(value="상세 기록", example = "상세 기록 예시")
        private String message;

        @ApiModelProperty(value="해당 기록의 칸 정보")
        private List<Location> matrices;

        @ApiModelProperty(value = "해당 운동 기록이 참여한 챌린지들")
        private List<ChallengeResponseDto.CInfoRes> challenges;
    }

    @Data @Builder
    static public class activityRecord {

        @ApiModelProperty(value="운동기록 id / 해당 운동 기록을 선택했을때 request 해야함", example = "1")
        private Long recordId;

        @ApiModelProperty(value="해당 기록의 칸의 수", example="9")
        private Long matrixNumber;

        @ApiModelProperty(value="해당 기록의 걸음의 수", example="150")
        private Integer stepCount;

        @ApiModelProperty(value="해당 기록의 거리", example="20")
        private Integer distance;

        @ApiModelProperty(value="시간", example="3분")
        private String exerciseTime;

        @ApiModelProperty(value="해당 기록의 시작 시간", example="12월 25일 금요일 18:04")
        private String started;
    }
}
