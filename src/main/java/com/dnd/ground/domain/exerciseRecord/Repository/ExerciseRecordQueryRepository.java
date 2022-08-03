package com.dnd.ground.domain.exerciseRecord.Repository;

import com.dnd.ground.domain.exerciseRecord.ExerciseRecord;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @description 운동 기록 query 인터페이스
 * @author  박세헌
 * @since   2022-08-01
 * @updated 2022-08-03 시간에 따른 운동기록 반환 로직을 리포지토리단에서 구현 : 박세헌
 */

public interface ExerciseRecordQueryRepository {
    List<ExerciseRecord> findRecordOfThisWeek(Long id);
    List<ExerciseRecord> findRecordOfPastByDay(Long id, LocalDateTime start);
    List<ExerciseRecord> findRecordOfPastByWeek(Long id, LocalDateTime start);
    List<ExerciseRecord> findChallengeRecordOfThisWeek(Long id, LocalDateTime start);
    List<ExerciseRecord> findChallengeRecordOfPast(Long id, LocalDateTime start);
}
