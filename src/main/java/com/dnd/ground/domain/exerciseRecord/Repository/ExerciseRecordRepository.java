package com.dnd.ground.domain.exerciseRecord.Repository;

import com.dnd.ground.domain.exerciseRecord.ExerciseRecord;
import com.dnd.ground.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @description 운동 기록 리포지토리 클래스
 * @author  박세헌
 * @since   2022-08-01
 * @updated 2022-08-01 / 생성
 */

public interface ExerciseRecordRepository extends JpaRepository<ExerciseRecord, Long>, ExerciseRecordQueryRepository {

    //회원이 특정 기간에 진행 중인 기록들 조회
    @Query("select e from ExerciseRecord e where e.user=:user and (e.started <=:toady and e.ended >=:today)")
    List<ExerciseRecord> findExerciseRecordByPeriod(@Param("user") User user, @Param("today") LocalDateTime today);

}
