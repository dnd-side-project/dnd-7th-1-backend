package com.dnd.ground.domain.exerciseRecord.Repository;

import com.dnd.ground.domain.exerciseRecord.ExerciseRecord;
import com.dnd.ground.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.Tuple;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @description 운동 기록 리포지토리 클래스
 * @author  박세헌
 * @since   2022-08-01
 * @updated 2022-08-012 / 1. findMatrixCount함수 ExerciseRecord단으로 이동
 *                        2. 유저와 친구들의 닉네임과 (start-end)사이 운동기록의 걸음 수 조회 함수
 *                         - 박세헌
 */

public interface ExerciseRecordRepository extends JpaRepository<ExerciseRecord, Long>, ExerciseRecordQueryRepository {

    // 유저와 친구들의 닉네임과 (start-end)사이 운동기록의 칸 수 조회
    @Query("select u.nickname, count(u) from User u " +
            "join u.exerciseRecords e " +
            "join e.matrices m " +
            "where u in :userAndFriends and e.started between :start and :end " +
            "group by u " +
            "order by count(u) desc ")
    List<Tuple> findMatrixCount(List<User> userAndFriends, LocalDateTime start, LocalDateTime end);

    // 유저와 친구들의 닉네임과 (start-end)사이 운동기록의 걸음 수 조회
    @Query("select u.nickname, sum(e.stepCount) from User u " +
            "join u.exerciseRecords e " +
            "where u in :userAndFriends and e.started between :start and :end " +
            "group by u " +
            "order by sum(e.stepCount) desc ")
    List<Tuple> findStepCount(List<User> userAndFriends, LocalDateTime start, LocalDateTime end);

}
