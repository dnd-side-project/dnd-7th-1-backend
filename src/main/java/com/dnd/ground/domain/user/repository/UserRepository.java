package com.dnd.ground.domain.user.repository;

import com.dnd.ground.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.Tuple;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @description 유저 리포지토리 인터페이스
 * @author  박세헌
 * @since   2022-08-01
 * @updated 2022-08-09 / 유저와 친구들의 닉네임과 이번주 칸의 수 조회 : 박세헌
 */

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByNickName(String nickname);

    @Query("select u.nickName, count(u) from User u " +
            "join u.exerciseRecords e " +
            "join e.matrices m " +
            "where u in :userAndFriends and e.started between :start and :end " +
            "group by u " +
            "order by count(u) desc ")
    List<Tuple> findMatrixCount(List<User> userAndFriends, LocalDateTime start, LocalDateTime end);

}
