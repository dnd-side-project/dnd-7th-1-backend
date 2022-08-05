package com.dnd.ground.domain.matrix.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Objects;

@Builder
@Data
public class MatrixSetDto{
    private Double latitude;
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