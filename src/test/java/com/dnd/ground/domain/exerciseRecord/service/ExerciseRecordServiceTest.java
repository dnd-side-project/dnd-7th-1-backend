package com.dnd.ground.domain.exerciseRecord.service;

import com.dnd.ground.domain.exerciseRecord.ExerciseRecord;
import com.dnd.ground.domain.matrix.Matrix;
import com.dnd.ground.domain.matrix.matrixService.MatrixService;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.service.UserService;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @description 이번주 기록 추출 테스트
 * @author  박세헌
 * @since   2022-08-01
 * @updated 2022-08-01 생성 : 박세헌
 */

@SpringBootTest
@Transactional
class ExerciseRecordServiceTest {

    @Autowired
    UserService userService;
    @Autowired
    ExerciseRecordService exerciseRecordService;
    @Autowired
    MatrixService matrixService;

    @Test
    void findRecordTest() {
        User user = User.builder()
                .userName("박세헌")
                .nickName("박세헌")
                .build();

        userService.save(user);

        Matrix m1 = new Matrix(1, 1, LocalDateTime.now());
        Matrix m2 = new Matrix(2, 2, LocalDateTime.now());

        matrixService.save(m1);
        matrixService.save(m2);

        // 이번주 기록
        ExerciseRecord er1 = new ExerciseRecord(user, LocalDateTime.of(2022, 8, 1, 12, 0, 0));
        er1.endedTime(LocalDateTime.of(2022, 8, 1, 12, 30, 0));

        // 지난주 기록
        ExerciseRecord er2 = new ExerciseRecord(user, LocalDateTime.of(2022, 7, 25, 12, 0, 0));
        er2.endedTime(LocalDateTime.of(2022, 7, 25, 12, 30, 0));

        er1.addMatrix(m1);
        er2.addMatrix(m2);

        exerciseRecordService.save(er1);
        exerciseRecordService.save(er2);

        List<ExerciseRecord> recordOfThisWeek = exerciseRecordService.findRecordOfThisWeek(user.getId());

        // 이번주 기록인 위도: 1.0, 경도: 1.0만 나와야 됨
        Assertions.assertThat(1).isEqualTo(recordOfThisWeek.size());
        Assertions.assertThat(1.0).isEqualTo(recordOfThisWeek.get(0).getMatrices().get(0).getLatitude());
        Assertions.assertThat(1.0).isEqualTo(recordOfThisWeek.get(0).getMatrices().get(0).getLongitude());
    }
}