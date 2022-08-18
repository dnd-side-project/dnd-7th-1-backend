package com.dnd.ground.domain.matrix;

import com.dnd.ground.domain.exerciseRecord.ExerciseRecord;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @description 운동 기록 엔티티
 * @author  박세헌
 * @since   2022-07-27
 * @updated 2022-08-17 / 위도, 경도 필드 Double형으로 변경 : 박세헌
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_record_id", nullable = false)
    private ExerciseRecord exerciseRecord;

    public Matrix(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // setExerciseRecord
    public void belongRecord(ExerciseRecord exerciseRecord) {
        this.exerciseRecord = exerciseRecord;
    }
}
