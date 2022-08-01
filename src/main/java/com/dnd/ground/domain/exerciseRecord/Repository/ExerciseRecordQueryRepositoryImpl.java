package com.dnd.ground.domain.exerciseRecord.Repository;

import com.dnd.ground.domain.exerciseRecord.ExerciseRecord;
import com.dnd.ground.domain.exerciseRecord.QExerciseRecord;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description 운동 기록 query 클래스(queryDsl 사용)
 *              1. findRecord: start와 end사이 운동기록 추출
 * @author  박세헌
 * @since   2022-08-01
 * @updated 2022-08-01 / 생성 : 박세헌
 */

@Repository
@RequiredArgsConstructor
public class ExerciseRecordQueryRepositoryImpl implements ExerciseRecordQueryRepository{
    private final JPAQueryFactory query;
    QExerciseRecord exerciseRecord = QExerciseRecord.exerciseRecord;

    public List<ExerciseRecord> findRecord(Long id, LocalDateTime start, LocalDateTime end){
        return query
                .select(exerciseRecord)
                .from(exerciseRecord)
                .where(QExerciseRecord.exerciseRecord.user.id.eq(id))
                .where(QExerciseRecord.exerciseRecord.started.between(start, end))
                .fetch();
    }

}
