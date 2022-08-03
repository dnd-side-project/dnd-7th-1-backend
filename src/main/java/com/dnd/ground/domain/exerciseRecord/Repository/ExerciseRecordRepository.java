package com.dnd.ground.domain.exerciseRecord.Repository;

import com.dnd.ground.domain.exerciseRecord.ExerciseRecord;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @description 운동 기록 리포지토리 클래스
 * @author  박세헌
 * @since   2022-08-01
 * @updated 2022-08-01 / 생성
 */

public interface ExerciseRecordRepository extends JpaRepository<ExerciseRecord, Long>, ExerciseRecordQueryRepository {

}
