package com.dnd.ground.domain.matrix.repository;

import com.dnd.ground.domain.matrix.dto.MatrixUserSet;
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
import static java.time.DayOfWeek.MONDAY;

/**
 * @description 운동 기록(영역) 관련 QueryDSL 레포지토리 (특정 범위 내 영역 조회)
 * @author  박찬호
 * @since   2023-02-14
 * @updated 1.다수의 회원들의 영역 조회용 쿼리 생성
 *          - 2023-02-15 박찬호
 */


@Slf4j
@RequiredArgsConstructor
public class MatrixRepositoryImpl implements MatrixRepositoryQuery {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Location> findMatrixPoint(User user, Location location, double spanDelta) {
        LocalDateTime start = LocalDateTime.now().with(MONDAY).withHour(0).withMinute(0).withSecond(0).withNano(0);

        return queryFactory
                .select(Projections.fields(Location.class,
                        Expressions.stringTemplate("ST_X({0})", matrix.point).castToNum(Double.class).as("latitude"),
                        Expressions.stringTemplate("ST_Y({0})", matrix.point).castToNum(Double.class).as("longitude")

                ))
                .from(matrix)
                .where(
                        recordInPeriod(user, start, LocalDateTime.now()),
                        containMBR(location, spanDelta)
                )
                .fetch();
    }

    public Map<User, List<Location>> findUsersMatrix(Set<User> users, Location location, double spanDelta) {
        LocalDateTime start = LocalDateTime.now().with(MONDAY).withHour(0).withMinute(0).withSecond(0).withNano(0);

        List<MatrixUserSet> queryResult = queryFactory
                .select(
                        Projections.constructor(MatrixUserSet.class,
                        Projections.fields(
                                Location.class,
                                Expressions.stringTemplate("ST_X({0})", matrix.point).castToNum(Double.class).as("latitude"),
                                Expressions.stringTemplate("ST_Y({0})", matrix.point).castToNum(Double.class).as("longitude")
                        ),
                        exerciseRecord.user
                        )
                )
                .from(matrix)
                .innerJoin(exerciseRecord)
                .on(
                        exerciseRecord.started.between(start, LocalDateTime.now()),
                        exerciseRecord.user.in(users),
                        matrix.exerciseRecord.eq(exerciseRecord)
                )
                .where(
                        containMBR(location, spanDelta)
                ).fetch();

        Map<User, List<Location>> result = new HashMap<>();

        for (MatrixUserSet matrixUserSet : queryResult) {
            List<Location> l = result.getOrDefault(matrixUserSet.getUser(), new ArrayList<>());
            l.add(matrixUserSet.getLocation());
            result.put(matrixUserSet.getUser(), l);

        }
        return result;

    }

    private BooleanExpression recordInPeriod(User user, LocalDateTime start, LocalDateTime end) {
        return matrix.exerciseRecord.in(
                JPAExpressions
                        .select(exerciseRecord)
                        .from(exerciseRecord)
                        .where(
                                exerciseRecord.user.eq(user),
                                exerciseRecord.started.between(start, end)
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
}
