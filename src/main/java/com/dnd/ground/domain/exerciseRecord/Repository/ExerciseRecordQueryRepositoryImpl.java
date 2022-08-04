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
 *              2. 개인 과거 기록(일)
 *              3. 개인 과거 기록(주)
 *              4. 챌린지 이번주 기록
 *              5. 챌린지 과거 기록
 * @author  박세헌
 * @since   2022-08-01
 * @updated 2022-08-03 / 시간에 따른 운동기록 반환 로직을 리포지토리단에서 구현 : 박세헌
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

    // 개인 과거 기록(일)
    // start: 해당 일 00:00:00 (request 받음)
    // end: 해당 일 23:59:59
    public List<ExerciseRecord> findRecordOfPastByDay(Long id, LocalDateTime start){
        LocalDateTime end = LocalDateTime.of(start.getYear(), start.getMonth(), start.getDayOfMonth(), 23, 59, 59);
        return findRecord(id, start, end);
    }

    // 개인 과거 기록(주)
    // start: 해당 주의 월요일 00:00:00 (request 받음)
    // end: 해당 주의 일요일 23:59:59
    public List<ExerciseRecord> findRecordOfPastByWeek(Long id, LocalDateTime start){
        LocalDateTime result = LocalDateTime.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        LocalDateTime end = LocalDateTime.of(result.getYear(), result.getMonth(), result.getDayOfMonth(), 23, 59, 59);
        return findRecord(id, start, end);
    }

    // 챌린지 이번주 기록(시작 ~ 지금)
    public List<ExerciseRecord> findChallengeRecordOfThisWeek(Long id, LocalDateTime start){
        return findRecord(id, start, LocalDateTime.now());
    }

    // 챌린지 과거 기록(시작 ~ 해당 주 일요일)
    // start: 챌린지 시작 시간 (request 받음)
    // end: 해당 주의 일요일 23:59:59
    public List<ExerciseRecord> findChallengeRecordOfPast(Long id, LocalDateTime start){
        LocalDateTime result = LocalDateTime.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        LocalDateTime end = LocalDateTime.of(result.getYear(), result.getMonth(), result.getDayOfMonth(), 23, 59, 59);
        return findRecord(id, start, end);
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

