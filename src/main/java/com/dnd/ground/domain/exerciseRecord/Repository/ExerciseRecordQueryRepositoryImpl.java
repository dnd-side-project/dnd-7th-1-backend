package com.dnd.ground.domain.exerciseRecord.Repository;

import com.dnd.ground.domain.exerciseRecord.ExerciseRecord;
import com.dnd.ground.domain.exerciseRecord.dto.QRecordDto;
import com.dnd.ground.domain.exerciseRecord.dto.QRecordDto_Stats;
import com.dnd.ground.domain.exerciseRecord.dto.RecordDto;
import com.dnd.ground.domain.matrix.dto.Location;
import com.dnd.ground.domain.user.User;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.dnd.ground.domain.exerciseRecord.QExerciseRecord.exerciseRecord;
import static com.dnd.ground.domain.matrix.QMatrix.matrix;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;

/**
 * @description 운동 기록 조회 관련 QueryDSL 레포지토리
 * @author  박찬호
 * @since   2023-03-01
 * @updated 1. 특정 기간 운동 기록의 정보 조회 쿼리 생성(걸음 수, 운동 시간 등)
 *          2023-03-05 박찬호
 */

@RequiredArgsConstructor
public class ExerciseRecordQueryRepositoryImpl implements ExerciseRecordQueryRepository {
    private final JPAQueryFactory queryFactory;

    /*특정 기간 내 운동 기록 조회*/
    @Override
    public List<RecordDto> findRecordInPeriod(User targetUser, LocalDateTime start, LocalDateTime end) {
        return queryFactory
                .select(
                        new QRecordDto(
                                exerciseRecord.distance,
                                exerciseRecord.exerciseTime,
                                exerciseRecord.stepCount,
                                exerciseRecord.started,
                                exerciseRecord.ended,
                                exerciseRecord.message
                        )
                )
                .from(exerciseRecord)
                .where(
                        exerciseRecord.user.eq(targetUser),
                        inPeriod(start, end)
                )
                .where()
                .fetch();
    }

    /*특정 기간 내 운동 기록 및 영역 조회*/
    @Override
    public Map<ExerciseRecord, List<Location>> findRecordWithLocation(User user, LocalDateTime started, LocalDateTime ended) {
        Map<ExerciseRecord, List<Point>> transform = queryFactory
                .from(exerciseRecord)
                .innerJoin(matrix)
                .on(
                        matrix.exerciseRecord.eq(exerciseRecord)
                )
                .where(
                        exerciseRecord.user.eq(user),
                        inPeriod(started, ended)
                )
                .transform(
                        groupBy(exerciseRecord).as(list(matrix.point))
                );

        return transform.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().stream()
                                .map(p -> new Location(p.getX(), p.getY()))
                                .collect(Collectors.toList())
                ));
    }

    /*특정 기간 내 운동 기록 관련 정보 조*/
    @Override
    public RecordDto.Stats getRecordCount(User user, LocalDateTime started, LocalDateTime ended) {
        return queryFactory
                .select(new QRecordDto_Stats(
                        exerciseRecord.stepCount.sum().castToNum(Long.class),
                        exerciseRecord.distance.sum().castToNum(Long.class),
                        exerciseRecord.exerciseTime.sum().castToNum(Long.class))
                )
                .from(exerciseRecord)
                .where(
                        exerciseRecord.user.eq(user),
                        inPeriod(started, ended)
                )
                .fetchFirst();
    }

    private BooleanExpression inPeriod(LocalDateTime started, LocalDateTime ended) {
        return started != null && ended != null ? exerciseRecord.started.goe(started).and(exerciseRecord.ended.loe(ended)) : null;
    }
}
