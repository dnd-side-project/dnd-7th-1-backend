package com.dnd.ground.domain.exerciseRecord.Repository;

import com.dnd.ground.domain.exerciseRecord.ExerciseRecord;
import com.dnd.ground.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @description 운동 기록 리포지토리 클래스
 * @author  박세헌, 박찬호
 * @since   2022-08-01
 * @updated 1.미사용 메소드 제거
 *          - 2023-03-05 박찬호
 *
 */

public interface ExerciseRecordRepository extends JpaRepository<ExerciseRecord, Long>, RankQueryRepository, ExerciseRecordQueryRepository {

    // 유저의 최근 활동 시간 조회
    @Query("select max(r.ended) from ExerciseRecord r where r.user=:user")
    Optional<LocalDateTime> findLastRecord(@Param("user") User user);

    // 운동 기록 날짜 조회 (중복 제거)
    @Query("select distinct function('date_format', e.started, '%Y-%m-%d') " +
            "from ExerciseRecord e where e.user = :user and e.started between :start and :end")
    List<String> findDayEventList(User user, LocalDateTime start, LocalDateTime end);

    //회원의 운동 기록 리스트 조회
    @Query("select e from ExerciseRecord e where e.user=:user")
    List<ExerciseRecord> findRecordsByUser(@Param("user") User user);
}
