package com.dnd.ground.domain.exerciseRecord.dto;

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
        private Long recordId;
        private String message;
    }
}
