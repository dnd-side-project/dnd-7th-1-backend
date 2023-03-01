package com.dnd.ground.domain.exerciseRecord.Repository;

import com.dnd.ground.domain.exerciseRecord.ExerciseRecord;
import com.dnd.ground.domain.exerciseRecord.dto.QRecordDto;
import com.dnd.ground.domain.exerciseRecord.dto.RecordDto;
import com.dnd.ground.domain.matrix.dto.Location;
import com.dnd.ground.domain.user.User;
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
 * @updated 1. 특정 기간 내 운동 기록 조회 쿼리 생성
 *          2. 특정 기간 내 운동 기록 및 영역 조회 쿼리 생성
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
                        exerciseRecord.started.between(start, end)
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
                        exerciseRecord.started.goe(started),
                        exerciseRecord.ended.loe(ended)
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
}
