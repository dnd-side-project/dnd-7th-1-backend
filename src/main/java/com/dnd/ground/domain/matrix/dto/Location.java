package com.dnd.ground.domain.matrix.dto;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * @description MySQL의 Point 자료형(위도, 경도)을 표현할 Location DTO
 * @author  박찬호
 * @since   2023.02.15
 * @updated 1. 클래스 생성
 *          - 2023.02.15 박찬호
 */
@Getter
@Setter
@NoArgsConstructor
public class Location {
    @ApiModelProperty(name = "위도", example = "37.3534200387494", required = true)
    private Double latitude;
    @ApiModelProperty(name = "경도", example = "126.83415058078452", required = true)
    private Double longitude;

    @QueryProjection
    public Location(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}

