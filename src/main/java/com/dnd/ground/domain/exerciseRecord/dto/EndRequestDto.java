package com.dnd.ground.domain.exerciseRecord.dto;

import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @description 기록 끝 Request Dto
 *              1. 운동기록 id
 *              2. matrix 정보
 *              3. 거리
 * @author  박세헌
 * @since   2022-08-02
 * @updated 2022-08-02 / 생성 : 박세헌
 */

@Data
public class EndRequestDto {
    private Long recordId;
    private List<RequestMatrix> matrices = new ArrayList<>();
    private Double distance;

    @Getter
    public static class RequestMatrix{
        private Double latitude;
        private Double longitude;
    }
}
