package com.dnd.ground.domain.matrix.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Objects;

/**
 * @description 중복 없는 칸에 대한 정보 dto
 * @author  박찬호, 박세헌
 * @since   2022-08-05
 * @updated 2022-08-05 / 생성: 박세헌
 */

@Data @Builder
public class MatrixSetDto {

    @ApiModelProperty(value = "위도", example = "37.123123")
    private Double latitude;

    @ApiModelProperty(value = "경도", example = "127.123123")
    private Double longitude;

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }

    @Override
    public boolean equals(Object obj) {
        if (this.getClass() != obj.getClass()) return false;
        return (Objects.equals(((MatrixSetDto) obj).latitude, this.latitude)) &&
                (Objects.equals(((MatrixSetDto) obj).longitude, this.longitude));
    }
}