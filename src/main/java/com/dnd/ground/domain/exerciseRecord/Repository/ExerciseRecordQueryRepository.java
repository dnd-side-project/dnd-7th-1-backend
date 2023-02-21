package com.dnd.ground.domain.exerciseRecord.Repository;

import com.dnd.ground.domain.exerciseRecord.ExerciseRecord;
import com.dnd.ground.domain.exerciseRecord.dto.RankCond;
import com.dnd.ground.domain.exerciseRecord.dto.RankDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @description 운동 기록 query 인터페이스
 * @author  박세헌, 박찬호
 * @since   2022-08-01
 * @updated 1.걸음수 랭킹 API 리팩토링 및 위치 변경
 *          2023-02-22 박찬호
 */

public interface ExerciseRecordQueryRepository {
    List<ExerciseRecord> findRecordOfThisWeek(Long id);
    List<ExerciseRecord> findRecord(Long id, LocalDateTime start, LocalDateTime end);
    List<RankDto> findRankMatrixRankAllTime(RankCond condition);
    List<RankDto> findRankArea(RankCond condition);
    List<RankDto> findRankStep(RankCond condition);
}
