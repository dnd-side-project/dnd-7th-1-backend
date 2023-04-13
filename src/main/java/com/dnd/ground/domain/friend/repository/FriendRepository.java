package com.dnd.ground.domain.friend.repository;

import com.dnd.ground.domain.friend.Friend;
import com.dnd.ground.domain.user.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * @description 친구와 관련한 레포지토리
 * @author  박찬호
 * @since   2022-08-01
 * @updated 1.친구 저장 방식 변경에 따라 상태 조회 쿼리 반환 타입 변경
 *          2.미사용 쿼리 제거(user로 친구 조회)
 *          - 2023.03.06 박찬호
 */

public interface FriendRepository extends JpaRepository<Friend, Long>, FriendQueryRepository {
    //요청 받은 친구 목록 조회
    @Query("SELECT f.user from Friend f where f.friend=:user and f.status='WAIT'")
    Slice<User> findReceiveRequest(@Param("user") User user, PageRequest pageRequest);

    @Query("select f from Friend f where f.user=:user and f.status='ACCEPT'")
    Slice<Friend> findFriendsByUserWithPaging(@Param("user") User user, PageRequest pageRequest);

    Optional<Friend> findById(@Param("id") Long id);

    //친구 관계 여부 조회
    @Query("select f from Friend f where (f.user=:user and f.friend=:friend) or (f.user=:friend and f.friend=:user)")
    List<Friend> findFriendRelation(@Param("user") User user, @Param("friend") User friend);

    //수락 여부 상관 없이 Friend 테이블에 있는 데이터 조회
    @Query("select f from Friend f where (f.friend =:user or f.user = :user)")
    List<Friend> findFriendsAnyway(@Param("user") User user);

    //요청 대기, 친구 상태인 친구 관계 조회
    @Query("select f from Friend f where" +
            " (f.user=:user and f.friend=:friend) or (f.user=:friend and f.friend=:user)" +
            "and f.status<>'REJECT'")
    Optional<Friend> findFriendInProgress(@Param("user") User user, @Param("friend") User friend);

    //요청 대기중인 친구 관계 조회
    @Query("select f from Friend f where f.user=:friend and f.friend=:user and f.status='WAIT'")
    Optional<Friend> findRequestFriend(@Param("user") User user, @Param("friend") User friend);

}
