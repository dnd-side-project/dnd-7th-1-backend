package com.dnd.ground.domain.user.dto;

import com.dnd.ground.domain.exerciseRecord.dto.RecordResponseDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @description 활동 기록 Response Dto
 * @author  박세헌, 박찬호
 * @since   2022-08-16
 * @updated 활동 기록 Response Dto 생성
 *          - 2022.08.16 박세헌
 */

@Data @Builder
public class ActivityRecordResponseDto {

    @ApiModelProperty(value = "활동 내역 정보")
    List<RecordResponseDto.activityRecord> activityRecords;

    @ApiModelProperty(value = "해당 날짜의 총 칸의 수", example = "27")
    Long totalMatrixNumber;

    @ApiModelProperty(value = "해당 날짜의 총 거리", example = "60")
    Integer totalDistance;

    @ApiModelProperty(value = "해당 날짜의 총 칸의 수", example = "540")
    Integer totalExerciseTime;
}
