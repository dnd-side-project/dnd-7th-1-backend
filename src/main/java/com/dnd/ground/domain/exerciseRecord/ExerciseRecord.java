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
 * @updated 2022-08-04 / cascade 조건 추가 : 박세헌
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
    private Double distance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "exerciseRecord", cascade = CascadeType.ALL)
    private List<Matrix> matrices = new ArrayList<>();

    public ExerciseRecord(User user, LocalDateTime started) {
        this.distance = 0.0;
        this.ended = LocalDateTime.now();
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

    // setDistance
    public void addDistance(Double distance){
        this.distance = distance;
    }
}
