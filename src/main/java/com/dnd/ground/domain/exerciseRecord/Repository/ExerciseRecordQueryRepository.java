package com.dnd.ground.domain.exerciseRecord.Repository;

import com.dnd.ground.domain.exerciseRecord.ExerciseRecord;
import com.dnd.ground.domain.exerciseRecord.dto.RecordDto;
import com.dnd.ground.domain.matrix.dto.Location;
import com.dnd.ground.domain.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @description 운동 기록 조회 관련 QueryDSL 레포지토리
 * @author  박찬호
 * @since   2023-03-01
 * @updated 1. 특정 기간 내 운동 기록 조회 쿼리 생성
 *          2. 특정 기간 내 운동 기록 및 영역 조회 쿼리 생성
 */
public interface ExerciseRecordQueryRepository {
    List<RecordDto> findRecordInPeriod(User targetUser, LocalDateTime start, LocalDateTime end);
    Map<ExerciseRecord, List<Location>> findRecordWithLocation(User user, LocalDateTime started, LocalDateTime ended);
}
