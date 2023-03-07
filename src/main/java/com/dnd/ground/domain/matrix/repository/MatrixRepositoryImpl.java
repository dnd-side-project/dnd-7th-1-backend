package com.dnd.ground.domain.matrix.repository;

import com.dnd.ground.domain.matrix.dto.MatrixCond;
import com.dnd.ground.domain.matrix.dto.QLocation;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.global.util.Direction;
import com.dnd.ground.global.util.GeometryUtil;
import com.dnd.ground.domain.matrix.dto.Location;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.*;

import static com.dnd.ground.domain.exerciseRecord.QExerciseRecord.exerciseRecord;
import static com.dnd.ground.domain.matrix.QMatrix.matrix;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;

/**
 * @description 운동 기록(영역) 관련 QueryDSL 레포지토리 (특정 범위 내 영역 조회)
 * @author  박찬호
 * @since   2023-02-14
 * @updated 1.중복을 제외한 영역 조회 쿼리 생성(Map)
 *          - 2023-03-07 박찬호
 */


@Slf4j
@RequiredArgsConstructor
public class MatrixRepositoryImpl implements MatrixRepositoryQuery {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Location> findMatrixList(MatrixCond condition) {
        return queryFactory
                .select(Projections.fields(Location.class,
                        Expressions.stringTemplate("ST_X({0})", matrix.point).castToNum(Double.class).as("latitude"),
                        Expressions.stringTemplate("ST_Y({0})", matrix.point).castToNum(Double.class).as("longitude")
                ))
                .from(matrix)
                .where(
                        recordInPeriodAndUser(condition.getUser(), condition.getStarted(), condition.getEnded()),
                        containMBR(condition.getLocation(), condition.getSpanDelta())
                )
                .fetch();
    }

    @Override
    public List<Location> findMatrixListDistinct(MatrixCond condition) {
        return queryFactory
                .select(Projections.fields(Location.class,
                        Expressions.stringTemplate("ST_X({0})", matrix.point).castToNum(Double.class).as("latitude"),
                        Expressions.stringTemplate("ST_Y({0})", matrix.point).castToNum(Double.class).as("longitude")
                ))
                .distinct()
                .from(matrix)
                .where(
                        recordInPeriodAndUser(condition.getUser(), condition.getStarted(), condition.getEnded()),
                        containMBR(condition.getLocation(), condition.getSpanDelta())
                )
                .fetch();
    }

    public Map<User, List<Location>> findMatrixMap(MatrixCond condition) {
        return queryFactory
                .from(matrix)
                .innerJoin(exerciseRecord)
                .on(
                        matrix.exerciseRecord.eq(exerciseRecord)
                )
                .where(
                        recordInUser(condition.getUser()),
                        recordInUsers(condition.getUsers()),
                        recordInPeriod(condition.getStarted(), condition.getEnded()),
                        containMBR(condition.getLocation(), condition.getSpanDelta())
                )
                .transform(
                        groupBy(exerciseRecord.user).as(
                                list(new QLocation(
                                        Expressions.stringTemplate("ST_X({0})", matrix.point).castToNum(Double.class).as("latitude"),
                                        Expressions.stringTemplate("ST_Y({0})", matrix.point).castToNum(Double.class).as("longitude")
                                )
                        )));
    }

    @Override
    public Map<User, List<Location>> findMatrixMapDistinct(MatrixCond condition) {
        return queryFactory
                .select(
                        exerciseRecord.user,
                        Expressions.stringTemplate("ST_X({0})", matrix.point).castToNum(Double.class).as("latitude"),
                        Expressions.stringTemplate("ST_Y({0})", matrix.point).castToNum(Double.class).as("longitude")
                )
                .distinct()
                .from(matrix)
                .innerJoin(exerciseRecord)
                .on(
                        matrix.exerciseRecord.eq(exerciseRecord)
                )
                .where(
                        recordInUser(condition.getUser()),
                        recordInUsers(condition.getUsers()),
                        recordInPeriod(condition.getStarted(), condition.getEnded()),
                        containMBR(condition.getLocation(), condition.getSpanDelta())
                )
                .transform(
                        groupBy(exerciseRecord.user).as(
                                list(new QLocation(
                                                Expressions.stringTemplate("ST_X({0})", matrix.point).castToNum(Double.class).as("latitude"),
                                                Expressions.stringTemplate("ST_Y({0})", matrix.point).castToNum(Double.class).as("longitude")
                                        )
                                )));
    }

    @Override
    public long matrixCount(MatrixCond condition) {
        return queryFactory
                .select(matrix.count())
                .from(matrix)
                .where(
                        recordInPeriodAndUser(condition.getUser(), condition.getStarted(), condition.getEnded())
                )
                .fetchFirst();
    }

    @Override
    public long matrixCountDistinct(MatrixCond condition) {
        return queryFactory
                .select(matrix.countDistinct())
                .from(matrix)
                .where(
                        recordInPeriodAndUser(condition.getUser(), condition.getStarted(), condition.getEnded())
                )
                .fetchFirst();
    }

    private BooleanExpression recordInPeriod(LocalDateTime start, LocalDateTime end) {
        return start != null && end != null ? exerciseRecord.ended.between(start, end) : null;
    }

    private BooleanExpression recordInPeriodAndUser(User user, LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) return recordInAllTime(user);
        else {
            return matrix.exerciseRecord.in(
                    JPAExpressions
                            .select(exerciseRecord)
                            .from(exerciseRecord)
                            .where(
                                    exerciseRecord.user.eq(user),
                                    exerciseRecord.started.between(start, end)
                            ));
        }
    }

    private BooleanExpression recordInAllTime(User user) {
        return matrix.exerciseRecord.in(
                JPAExpressions
                        .select(exerciseRecord)
                        .from(exerciseRecord)
                        .where(
                                exerciseRecord.user.eq(user)
                        )
        );
    }

    private BooleanExpression containMBR(Location location, Double spanDelta) {
        if (location == null) return null;
        Location northEast = GeometryUtil.calculate(location, spanDelta, Direction.NORTHEAST);
        Location southWest = GeometryUtil.calculate(location, spanDelta, Direction.SOUTHWEST);

        return Expressions.booleanTemplate("function('MBRContains', {0}, {1})",
                        String.format("LINESTRING(%f %f, %f %f)",
                                northEast.getLatitude(), northEast.getLongitude(),
                                southWest.getLatitude(), southWest.getLongitude()
                        ),
                        matrix.point)
                .eq(true);
    }

    private BooleanExpression recordInUser(User user) {
        return user != null ? exerciseRecord.user.eq(user) : null;
    }

    private BooleanExpression recordInUsers(Set<User> users) {
        return users != null ? exerciseRecord.user.in(users) : null;
    }
}
