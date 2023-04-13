package com.dnd.ground.domain.user.repository;

import com.dnd.ground.domain.exerciseRecord.ExerciseRecord;
import com.dnd.ground.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * @description 유저 리포지토리 인터페이스
 * @author  박찬호
 * @since   2022-08-01
 * @updated 1.User-UserProperty 분리에 따른 쿼리 수정
 *          2.fetch join을 활용해 N+1 방지
 *          - 2023-03-20 박찬호
 */

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByNickname(String nickname);

    @Query("SELECT u FROM User u JOIN FETCH u.property WHERE u.nickname = :nickname")
    Optional<User> findByNicknameWithProperty(@Param("nickname") String nickname);

    @Query("select u from User u join u.exerciseRecords e where e = :exerciseRecord")
    Optional<User> findByExerciseRecord(ExerciseRecord exerciseRecord);

    @Query("select u from User u where u.id=:id")
    Optional<User> findByKakaoId(Long id);
    Optional<User> findByEmail(String email);

    @Transactional
    void deleteByNickname(String nickname);
    @Transactional
    void deleteByEmail(String email);
}
