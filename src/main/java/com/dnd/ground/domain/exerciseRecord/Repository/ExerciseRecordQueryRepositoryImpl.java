package com.dnd.ground.domain.exerciseRecord.Repository;

import com.dnd.ground.domain.exerciseRecord.ExerciseRecord;

import com.dnd.ground.domain.exerciseRecord.dto.RankCond;
import com.dnd.ground.domain.exerciseRecord.dto.RankDto;
import com.dnd.ground.global.exception.ExceptionCodeSet;
import com.dnd.ground.global.exception.ExerciseRecordException;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.OrderSpecifier;
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
 * @description 운동 기록(영역) 관련 QueryDSL 레포지토리
 * @author  박세헌, 박찬호
 * @since   2022-08-01
 * @updated 1.쿼리를 실행하는 공통 부분 메소드로 분리
 *          2.걸음수 랭킹 API 리팩토링 및 위치 변경
 *          2023-02-22 박찬호
 */

@Repository
@Slf4j
@RequiredArgsConstructor
public class ExerciseRecordQueryRepositoryImpl implements ExerciseRecordQueryRepository {
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

    /**
     * 랭킹 쿼리 실행 메소드
     * @param constructor 조회할 랭킹 종류(Matrix, Area, Step ..)
     * @param order 정렬 기준(조회하는 데이터 기준)
     * @param tableJoinCondition 테이블 조인 조건(기간 등)
     * @param condition 검색 조건
     * @return 랭킹 결과
     */
    private List<RankDto> execQuery(ConstructorExpression<RankDto> constructor, OrderSpecifier<Long> order,
                                    BooleanExpression tableJoinCondition, RankCond condition) {
        return queryFactory
                .select(constructor)
                .from(user)
                .leftJoin(exerciseRecord)
                .on(tableJoinCondition)
                .leftJoin(matrix)
                .on(matrix.exerciseRecord.eq(exerciseRecord))
                .where(user.in(condition.getUsers()))
                .groupBy(user.nickname)
                .orderBy(order)
                .fetch();
    }

    @Override
    public List<RankDto> findRankMatrixRankAllTime(RankCond condition) {
        ConstructorExpression<RankDto> constructor = Projections.constructor(RankDto.class,
                user.nickname,
                user.picturePath,
                matrix.count()
        );

        BooleanExpression tableJoinCondition = exerciseRecord.user.eq(user).and(allTime());
        OrderSpecifier<Long> order = matrix.count().desc();
        return execQuery(constructor, order, tableJoinCondition, condition);
    }

    @Override
    public List<RankDto> findRankArea(RankCond condition) {
        if (condition.getStarted() == null || condition.getEnded() == null) throw new ExerciseRecordException(ExceptionCodeSet.INVALID_TIME);

        ConstructorExpression<RankDto> constructor = Projections.constructor(
                RankDto.class,
                user.nickname,
                user.picturePath,
                matrix.countDistinct()
        );

        BooleanExpression tableJoinCondition = userEqAndInPeriod(condition.getStarted(), condition.getEnded());
        OrderSpecifier<Long> order = matrix.countDistinct().desc();
        return execQuery(constructor, order, tableJoinCondition, condition);
    }

    @Override
    public List<RankDto> findRankStep(RankCond condition) {
        if (condition.getStarted() == null || condition.getEnded() == null) throw new ExerciseRecordException(ExceptionCodeSet.INVALID_TIME);

        return queryFactory
                .select(Projections.constructor(RankDto.class,
                        user.nickname,
                        user.picturePath,
                        exerciseRecord.stepCount.sum()
                                .castToNum(Long.class)
                                .coalesce(0L)
                ))
                .from(user)
                .leftJoin(exerciseRecord)
                .on(userEqAndInPeriod(condition.getStarted(), condition.getEnded()))
                .where(user.in(condition.getUsers()))
                .groupBy(user.nickname)
                .orderBy(exerciseRecord.stepCount.sum()
                        .castToNum(Long.class)
                        .desc())
                .fetch();
    }

    private BooleanExpression userEqAndInPeriod(LocalDateTime started, LocalDateTime ended) {
        return started != null && ended != null ?
                exerciseRecord.user.eq(user).and(exerciseRecord.started.after(started))
                        .and(exerciseRecord.ended.before(ended))
                :
                null;
    }

    private BooleanExpression allTime() {
        return exerciseRecord.started.after(user.created)
                .and(exerciseRecord.ended.before(LocalDateTime.now()));
    }
}