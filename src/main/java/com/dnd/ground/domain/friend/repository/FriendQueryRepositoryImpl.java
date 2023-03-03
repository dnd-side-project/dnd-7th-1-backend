package com.dnd.ground.domain.friend.repository;

import com.dnd.ground.domain.friend.Friend;
import com.dnd.ground.domain.friend.FriendStatus;
import com.dnd.ground.domain.friend.dto.FriendCondition;
import com.dnd.ground.domain.user.User;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.dnd.ground.domain.friend.QFriend.friend1;

/**
 * @description 친구 조회 레포지토리
 * @author  박찬호
 * @since   2023.02.15
 * @updated 1. 친구 목록 조회 동적 쿼리 생성
 *          - 2023.02.15 박찬호
 */

@RequiredArgsConstructor
public class FriendQueryRepositoryImpl implements FriendQueryRepository {
    private final JPAQueryFactory queryFactory;

    //친구 목록 조회
    @Override
    public List<User> findFriends(FriendCondition condition) {
        return queryFactory
                .select(friend1.friend)
                .from(friend1)
                .where(
                        userEq(condition.getUser()),
                        friendEq(condition.getFriend()),
                        statusEq(condition.getStatus())
                )
                .fetch();
    }

    private BooleanExpression userEq(User user) {
        return user != null ? friend1.user.eq(user) : null;
    }

    private BooleanExpression friendEq(User friend) {
        return friend != null ? friend1.friend.eq(friend) : null;
    }

    private BooleanExpression statusEq(FriendStatus status) {
        return status != null ? friend1.status.eq(status) : null;
    }
}