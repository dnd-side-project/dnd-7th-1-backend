package com.dnd.ground.domain.friend.repository;

import com.dnd.ground.domain.friend.Friend;
import com.dnd.ground.domain.friend.FriendStatus;
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
 * @updated 1.요청받은 친구 목록 조회 쿼리 추가
 *          - 2022.10.29 박찬호
 */

public interface FriendRepository extends JpaRepository<Friend, Long> {

    //User를 통해 친구 목록 조회
    @Query("select f from Friend f where (f.friend =:user or f.user = :user) and f.status='Accept'")
    List<Friend> findFriendsByUser(@Param("user") User user);

    //친구 요청 리스트 조회
    @Query("select f.user from User u inner join Friend f on f.friend=:user and f.status='Wait' where f.friend=u")
    Slice<User> findReceiveRequest(@Param("user") User user, PageRequest pageRequest);

    Slice<Friend> findFriendsByUserOrFriendAndStatus(@Param("user") User user, @Param("friend") User friend, @Param("status")FriendStatus status, PageRequest pageRequest);

    Optional<Friend> findById(Long id);

    //친구 관계 여부 조회
    @Query("select f from Friend f where (f.user=:user and f.friend=:friend) or (f.user=:friend and f.friend=:user)")
    Optional<Friend> findFriendRelation(@Param("user") User user, @Param("friend") User friend);

    //수락 여부 상관 없이 Friend 테이블에 있는 데이터 조회
    @Query("select f from Friend f where (f.friend =:user or f.user = :user)")
    List<Friend> findFriendsAnyway(@Param("user") User user);

    //요청 대기, 친구 상태인 친구 관계 조회
    @Query("select f from Friend f where" +
            " (f.user=:user and f.friend=:friend) or (f.user=:friend and f.friend=:user)" +
            "and f.status<>'Reject'")
    Optional<Friend> findFriendInProgress(@Param("user") User user, @Param("friend") User friend);

    //요청 대기중인 친구 관계 조회
    @Query("select f from Friend f where f.user=:user and f.friend=:friend and f.status='Wait'")
    Optional<Friend> findRequestFriend(@Param("user") User user, @Param("friend") User friend);

}
