package com.dnd.ground.domain.exerciseRecord.Repository;

import com.dnd.ground.domain.exerciseRecord.ExerciseRecord;
import com.dnd.ground.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.Tuple;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @description 운동 기록 리포지토리 클래스
 * @author  박세헌
 * @since   2022-08-01
 * @updated 2022-08-16 / 1.유저의 마지막 활동 조회
 *                         - 박찬호
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

    // 유저의 최근 활동 시간 조회
    @Query("select max(r.ended) from ExerciseRecord r where r.user=:user")
    Optional<LocalDateTime> findLastRecord(@Param("user") User user);
}
