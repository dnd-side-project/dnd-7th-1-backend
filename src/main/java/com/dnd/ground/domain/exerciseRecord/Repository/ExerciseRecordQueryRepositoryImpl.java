package com.dnd.ground.domain.exerciseRecord.Repository;

import com.dnd.ground.domain.exerciseRecord.ExerciseRecord;

import com.dnd.ground.domain.exerciseRecord.dto.ExerciseCond;
import com.dnd.ground.domain.exerciseRecord.dto.RankDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import static com.dnd.ground.domain.exerciseRecord.QExerciseRecord.exerciseRecord;
import static com.dnd.ground.domain.matrix.QMatrix.matrix;
import static com.dnd.ground.domain.user.QUser.user;

/**
 * @description 운동 기록 query 클래스(queryDsl 사용)
 *              1. 개인 이번주 기록
                2. start-end 사이 운동기록
 * @author  박세헌, 박찬호
 * @since   2022-08-01
 * @updated 1.누적 랭킹 조회 리팩토링
 *          2023-02-21
 */

@Repository
@Slf4j
@RequiredArgsConstructor
public class ExerciseRecordQueryRepositoryImpl implements ExerciseRecordQueryRepository{
    private final JPAQueryFactory queryFactory;

    // 개인 이번주 기록 (이번주 월요일 ~ 지금)
    public List<ExerciseRecord> findRecordOfThisWeek(Long id){
        LocalDateTime result = LocalDateTime.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDateTime start = LocalDateTime.of(result.getYear(), result.getMonth(), result.getDayOfMonth(), 0, 0, 0);
        return findRecord(id, start, LocalDateTime.now());
    }

    public List<ExerciseRecord> findRecord(Long id, LocalDateTime start, LocalDateTime end){
        return queryFactory
                .select(exerciseRecord)
                .from(exerciseRecord)
                .where(exerciseRecord.user.id.eq(id))
                .where(exerciseRecord.started.between(start, end))
                .fetch();
    }

    @Override
    public List<RankDto> findRankMatrixRankAllTime(ExerciseCond condition) {
        List<RankDto> fetch = queryFactory
                .select(Projections.constructor(RankDto.class,
                        user.nickname,
                        user.picturePath,
                        matrix.count()
                ))
                .from(user)
                .leftJoin(exerciseRecord)
                .on(
                        exerciseRecord.user.eq(user),
                        allTime()
                )
                .leftJoin(matrix)
                .on(matrix.exerciseRecord.eq(exerciseRecord))
                .where(user.in(condition.getUsers()))
                .groupBy(user.nickname)
                .orderBy(matrix.count().desc())
                .fetch();
        log.info("결과:{}", fetch.toString());
        return fetch;
    }

    private BooleanExpression inPeriod(LocalDateTime started, LocalDateTime ended) {
        return started != null && ended != null ?
                exerciseRecord.started.after(started)
                        .and(exerciseRecord.ended.before(ended))
                :
                null;
    }

    private BooleanExpression allTime() {
        return exerciseRecord.started.after(user.created)
                .and(exerciseRecord.ended.before(LocalDateTime.now()));
    }
}

