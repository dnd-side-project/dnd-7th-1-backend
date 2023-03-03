package com.dnd.ground.domain.matrix;

import com.dnd.ground.domain.exerciseRecord.ExerciseRecord;
import com.dnd.ground.global.util.GeometryUtil;
import lombok.*;
import org.locationtech.jts.geom.Point;

import javax.persistence.*;

/**
 * @description 운동 기록 엔티티
 * @author  박세헌, 박찬호
 * @since   2022-07-27
 * @updated 1. Point 추가 (위도, 경도에서 point로 이관 중)
 *          - 2023.02.08 박찬호
 */

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="matrix")
@Entity
public class Matrix {

    @Id @GeneratedValue
    @Column(name = "matrix_id")
    private Long id;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column
    private Point point;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_record_id", nullable = false)
    private ExerciseRecord exerciseRecord;

    public Matrix(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.point = GeometryUtil.coordinateToPoint(latitude, longitude);
    }

    // setExerciseRecord
    public void belongRecord(ExerciseRecord exerciseRecord) {
        this.exerciseRecord = exerciseRecord;
    }
}
