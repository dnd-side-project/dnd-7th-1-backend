package com.dnd.ground.domain.matrix;

import com.dnd.ground.domain.exerciseRecord.ExerciseRecord;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @description 운동 기록 엔티티
 * @author  박세헌
 * @since   2022-07-27
 * @updated 2022-08-01 / 비즈니스 로직 추가 : 박세헌
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
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Column(nullable = false)
    private LocalDateTime created;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_record_id")
    private ExerciseRecord exerciseRecord;

    public Matrix(double latitude, double longitude, LocalDateTime created) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.created = created;
    }

    // setExerciseRecord
    public void belongRecord(ExerciseRecord exerciseRecord) {
        this.exerciseRecord = exerciseRecord;
    }
}
