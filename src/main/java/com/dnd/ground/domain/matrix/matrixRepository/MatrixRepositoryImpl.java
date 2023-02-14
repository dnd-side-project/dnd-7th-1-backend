package com.dnd.ground.domain.matrix.matrixRepository;

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
import java.util.List;

import static com.dnd.ground.domain.exerciseRecord.QExerciseRecord.exerciseRecord;
import static com.dnd.ground.domain.matrix.QMatrix.matrix;
import static java.time.DayOfWeek.MONDAY;

/**
 * @description 운동 기록(영역) 관련 QueryDSL 레포지토리
 * @author  박찬호
 * @since   2023-02-14
 * @updated 1.일정 거리 내 영역 조회 구현
 *          - 2023-02-14 박찬호
 */


@Slf4j
@RequiredArgsConstructor
public class MatrixRepositoryImpl implements MatrixRepositoryQuery {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Location> findMatrixPoint(User user, Location location) {
        LocalDateTime start = LocalDateTime.now().with(MONDAY).withHour(0).withMinute(0).withSecond(0).withNano(0);

        return queryFactory
                .select(Projections.fields(Location.class,
                        Expressions.stringTemplate("ST_X({0})", matrix.point).castToNum(Double.class).as("latitude"),
                        Expressions.stringTemplate("ST_Y({0})", matrix.point).castToNum(Double.class).as("longitude")

                ))
                .from(matrix)
                .where(
                        recordInPeriod(user, start, LocalDateTime.now()),
                        containMBR(location)
                )
                .fetch();
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
    private BooleanExpression containMBR(Location location) {
        if (location == null) return null;
        Double lat = location.getLatitude();
        Double lon = location.getLongitude();

        Location northEast = GeometryUtil.calculate(lat, lon, 3.0, Direction.NORTHEAST);
        Location southWest = GeometryUtil.calculate(lat, lon, 3.0, Direction.SOUTHWEST);

        return Expressions.booleanTemplate("function('MBRContains', {0}, {1})",
                        String.format("LINESTRING(%f %f, %f %f)",
                                northEast.getLatitude(), northEast.getLongitude(),
                                southWest.getLatitude(), southWest.getLongitude()
                        ),
                        matrix.point)
                .eq(true);
    }
}
