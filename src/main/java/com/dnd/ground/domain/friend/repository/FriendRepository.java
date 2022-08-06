package com.dnd.ground.domain.friend.repository;

import com.dnd.ground.domain.friend.Friend;
import com.dnd.ground.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * @description 친구와 관련한 레포지토리
 * @author  박찬호
 * @since   2022-08-01
 * @updated 1. Accept 상태의 친구 목록 조회 쿼리 생성
 *          - 2022.08.02 박찬호
 */

public interface FriendRepository extends JpaRepository<Friend, Long> {

    //User를 통해 친구 목록 조회
    @Query("select f from Friend f where (f.friend =:user or f.user = :user) and f.status='Accept'")
    List<Friend> findFriendsById(@Param("user") User user);

    Optional<Friend> findById(Long id);
}
