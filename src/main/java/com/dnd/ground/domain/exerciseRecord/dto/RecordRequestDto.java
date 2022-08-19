package com.dnd.ground.domain.exerciseRecord.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description 운동기록 Request Dto
 * @author  박세헌
 * @since   2022-08-16
 * @updated 생성 / 2022-08-18 박세헌
 */

@Data
public class RecordRequestDto {
    @Data
    static public class Message{

        @ApiModelProperty(value="운동 기록 id", required = true)
        private Long recordId;

        @ApiModelProperty(value="운동 기록 상세 메시지")
        private String message;
    }
}
