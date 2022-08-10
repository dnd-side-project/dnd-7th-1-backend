package com.dnd.ground.domain.exerciseRecord.Repository;

import com.dnd.ground.domain.exerciseRecord.ExerciseRecord;
import com.dnd.ground.domain.exerciseRecord.QExerciseRecord;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description 운동 기록 query 클래스(queryDsl 사용)
 *              1. 개인 이번주 기록
                2. start-end 사이 운동기록
 * @author  박세헌
 * @since   2022-08-01
 * @updated 2022-08-09 / 2022-08-09 과거 기록 조회 삭제(보류) : 박세헌
 */

@Repository
@RequiredArgsConstructor
public class ExerciseRecordQueryRepositoryImpl implements ExerciseRecordQueryRepository{
    private final JPAQueryFactory query;
    QExerciseRecord exerciseRecord = QExerciseRecord.exerciseRecord;

    // 개인 이번주 기록 (이번주 월요일 ~ 지금)
    public List<ExerciseRecord> findRecordOfThisWeek(Long id){
        LocalDateTime result = LocalDateTime.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDateTime start = LocalDateTime.of(result.getYear(), result.getMonth(), result.getDayOfMonth(), 0, 0, 0);
        return findRecord(id, start, LocalDateTime.now());
    }

    public List<ExerciseRecord> findRecord(Long id, LocalDateTime start, LocalDateTime end){
        return query
                .select(exerciseRecord)
                .from(exerciseRecord)
                .where(QExerciseRecord.exerciseRecord.user.id.eq(id))
                .where(QExerciseRecord.exerciseRecord.started.between(start, end))
                .fetch();
    }
}

