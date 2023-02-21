package com.dnd.ground.domain.exerciseRecord.Repository;

import com.dnd.ground.domain.exerciseRecord.ExerciseRecord;
import com.dnd.ground.domain.exerciseRecord.dto.ExerciseCond;
import com.dnd.ground.domain.exerciseRecord.dto.RankDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @description 운동 기록 query 인터페이스
 * @author  박세헌, 박찬호
 * @since   2022-08-01
 * @updated 1.누적 랭킹 조회 리팩토링
 *          2023-02-21
 */

public interface ExerciseRecordQueryRepository {
    List<ExerciseRecord> findRecordOfThisWeek(Long id);
    List<ExerciseRecord> findRecord(Long id, LocalDateTime start, LocalDateTime end);
    List<RankDto> findRankMatrixRankAllTime(ExerciseCond condition);
}
