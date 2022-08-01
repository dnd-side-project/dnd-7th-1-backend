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
 * @updated 2022-08-01 / 비즈니스 로직 추가 : 박세헌
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "exerciseRecord")
    private List<Matrix> matrices = new ArrayList<>();

    public ExerciseRecord(User user, LocalDateTime started) {
        this.user = user;
        this.started = started;
    }

    // setMatrix
    public void addMatrix(Matrix matrix){
        this.matrices.add(matrix);
        matrix.belongRecord(this);
    }

    // setEnded
    public void endedTime(LocalDateTime ended){
        this.ended = ended;
    }
}
