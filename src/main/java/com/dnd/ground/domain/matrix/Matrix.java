package com.dnd.ground.domain.matrix;

import com.dnd.ground.domain.exerciseRecord.ExerciseRecord;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @description 운동 기록 엔티티
 * @author  박찬호, 박세헌
 * @since   2022-07-27
 * @updated 2022-07-27 / 기록 영역 생성 및 연관 관계 매핑 :박세헌
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_record_id")
    private ExerciseRecord exerciseRecord;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Column(nullable = false)
    private LocalDateTime created;

}
