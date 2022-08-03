package com.dnd.ground.domain.exerciseRecord.service;

import com.dnd.ground.domain.exerciseRecord.ExerciseRecord;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @description 운동 기록 서비스 인터페이스
 * @author  박세헌
 * @since   2022-08-01
 * @updated 2022-08-01 / 생성 : 박세헌
 */

public interface ExerciseRecordService {

    ExerciseRecord save(ExerciseRecord exerciseRecord);

    ExerciseRecord findById(Long id);

    void delete(Long exerciseRecordId);

    List<ExerciseRecord> findRecordOfThisWeek(Long id);

    List<ExerciseRecord> findRecordOfPastByDay(Long id, LocalDateTime start);

    List<ExerciseRecord> findRecordOfPastByWeek(Long id, LocalDateTime start);

    List<ExerciseRecord> findChallengeRecordOfThisWeek(Long id, LocalDateTime start);

    List<ExerciseRecord> findChallengeRecordOfPast(Long id, LocalDateTime start);

}
