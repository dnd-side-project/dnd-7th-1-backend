package com.dnd.ground.domain.friend.repository;

import com.dnd.ground.domain.friend.Friend;
import com.dnd.ground.domain.friend.vo.FriendSearchVo;
import com.dnd.ground.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @description 친구와 관련한 레포지토리
 * @author  박찬호
 * @since   2022-08-01
 * @updated 1.모든 친구 관계 조회 쿼리 생성
 *          - 2023.05.25 박찬호
 */

public interface FriendRepository extends JpaRepository<Friend, Long>, FriendQueryRepository {
    //친구 관계 여부 조회
    @Query("SELECT f " +
            "FROM Friend f " +
            "WHERE (f.user=:user AND f.friend=:friend) " +
                "OR (f.user=:friend AND f.friend=:user)")
    List<Friend> findFriendRelation(@Param("user") User user, @Param("friend") User friend);

    @Modifying
    @Transactional
    @Query("DELETE FROM Friend f " +
            "WHERE f.user = :user OR f.friend = :user")
    void deleteAllByUser(@Param("user") User user);

    @Modifying
    @Transactional
    @Query("DELETE FROM Friend f " +
            "WHERE (f.user = :user AND f.friend IN :friend) " +
                "OR " +
                "(f.user IN :friend AND f.friend = :user)")
    int deleteBulk(@Param("user") User user, @Param("friend") List<User> friend);

    //요청 대기, 친구 상태인 친구 관계 조회
    @Query("SELECT f FROM Friend f " +
            "WHERE (f.user=:user AND f.friend=:friend) " +
                "OR (f.user=:friend AND f.friend=:user) " +
                "AND f.status<>'REJECT'")
    List<Friend> findFriendInProgress(@Param("user") User user, @Param("friend") User friend);

    //요청 대기중인 친구 관계 조회
    @Query("SELECT f " +
            "FROM Friend f " +
            "WHERE f.user=:friend " +
                "AND f.friend=:user " +
                "AND f.status='WAIT'")
    Optional<Friend> findRequestFriend(@Param("user") User user, @Param("friend") User friend);

    //네모두 친구 검색
    @Query(value = "SELECT u.nickname, u.picture_path as picturePath " +
            "FROM friend f " +
            "INNER JOIN user u " +
            "ON f.friend_id = u.user_id " +
            "WHERE f.friend_status = 'ACCEPT' " +
            "AND f.user_id = ?1 " +
            "AND MATCH(u.nickname) AGAINST(?2 IN BOOLEAN MODE)", nativeQuery = true)
    List<FriendSearchVo> searchWithFullTextIdx(@Param("userId") Long userId , @Param("keyword") String keyword);

    //모든 친구 관계 조회
    @Query("SELECT f " +
            "FROM Friend f " +
            "WHERE f.user = :user " +
                "OR f.friend = :user")
    List<Friend> findAllFriends(@Param("user") User user);
}
