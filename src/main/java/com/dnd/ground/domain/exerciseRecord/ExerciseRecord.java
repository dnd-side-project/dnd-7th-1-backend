package com.dnd.ground.domain.exerciseRecord;

import com.dnd.ground.domain.matrix.Matrix;
import com.dnd.ground.domain.user.User;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @description 운동 기록 엔티티
 * @author  박찬호, 박세헌
 * @since   2022-07-27
 * @updated 2022-08-16 / minute(분) 삭세 - 박세헌
 */

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="exercise_record")
@Entity
public class ExerciseRecord {

    @Id @GeneratedValue
    @Column(name = "exercise_record_id")
    private Long id;

    @Column(nullable = false)
    private LocalDateTime started;

    @Column(nullable = false)
    private LocalDateTime ended;

    @Column(nullable = false)
    private Integer distance;

    @Column(nullable = false)
    private Integer exerciseTime;

    @Column(nullable = false)
    private Integer stepCount;

    @Column(name = "record_message", columnDefinition = "varchar(100)")
    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "exerciseRecord", cascade = CascadeType.ALL)
    private List<Matrix> matrices = new ArrayList<>();

    public ExerciseRecord(User user, LocalDateTime started) {
        this.distance = 0;
        this.exerciseTime = 0;
        this.stepCount = 0;
        this.ended = LocalDateTime.now();
        this.user = user;
        this.started = started;
    }

    // 칸 추가
    public void addMatrix(Matrix matrix){
        this.matrices.add(matrix);
        matrix.belongRecord(this);
    }

    // 정보 추가
    public void updateInfo(Integer distance, Integer stepCount, Integer second, String message){
        this.ended = LocalDateTime.now();
        this.distance = distance;
        this.stepCount = stepCount;
        this.exerciseTime = second;
        this.message = message;
    }

}
