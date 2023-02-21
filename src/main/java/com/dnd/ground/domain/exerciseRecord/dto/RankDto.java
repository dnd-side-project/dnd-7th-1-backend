package com.dnd.ground.domain.exerciseRecord.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @description QueryDSL에서 받아올 랭킹 관련 DTO
 * @author  박찬호
 * @since   2023-02-19
 * @updated 1.클래스 생성
 *          - 2023-02-19 박찬호
 */

@AllArgsConstructor
@Setter
@Getter
public class RankDto {
    private String nickname;
    private String picturePath;
    private Long score;
}
