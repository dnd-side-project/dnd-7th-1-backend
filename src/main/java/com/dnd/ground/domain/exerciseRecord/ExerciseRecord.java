package com.dnd.ground.domain.exerciseRecord;

import com.dnd.ground.domain.matrix.Matrix;
import com.dnd.ground.domain.user.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="exercise_record")
@Entity
public class ExerciseRecord {

    @Id @GeneratedValue
    @Column(name = "exercise_record_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "exerciseRecord")
    private List<Matrix> matrices = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime duration;

    @Column(nullable = false)
    private LocalDateTime started;

    @Column(nullable = false)
    private LocalDateTime ended;

    @Column(nullable = false)
    private Long durationMatrix;
}
