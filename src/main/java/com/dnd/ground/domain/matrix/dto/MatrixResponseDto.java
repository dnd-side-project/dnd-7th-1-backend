package com.dnd.ground.domain.matrix.dto;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.util.List;
/**
 * @description 특정 영역을 조회하기 위한 Response DTO
 * @author  박찬호
 * @since   2023.03.12
 * @updated 1. 클래스 생성
 *          - 2023.03.12 박찬호
 */
@Getter
public class MatrixResponseDto {
    @ApiModelProperty(value="닉네임", example="NickA")
    private String nickname;
    @ApiModelProperty(value="영역", example="[{\"latitude\":32.123,\"longitude\":123.456},{\"latitude\":32.123,\"longitude\":123.456}]")
    private List<Location> matrices;

    @QueryProjection
    public MatrixResponseDto(String nickname, List<Location> matrices) {
        this.nickname = nickname;
        this.matrices = matrices;
    }
}
