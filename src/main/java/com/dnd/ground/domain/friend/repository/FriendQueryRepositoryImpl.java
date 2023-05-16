package com.dnd.ground.domain.friend.repository;

import com.dnd.ground.domain.friend.FriendStatus;
import com.dnd.ground.domain.friend.dto.*;
import com.dnd.ground.domain.matrix.dto.Location;
import com.dnd.ground.domain.user.User;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.dnd.ground.domain.friend.QFriend.friend1;
import static com.dnd.ground.domain.user.QUser.user;
import static com.dnd.ground.domain.user.QUserProperty.userProperty;

/**
 * @description 친구 조회 레포지토리
 * @author  박찬호
 * @since   2023.02.15
 * @updated 1. 네모두 추천 친구 쿼리 구현
 *          2. 친구 목록 조회 쿼리 개선 (No offset)
 *          - 2023.05.16 박찬호
 */

@RequiredArgsConstructor
public class FriendQueryRepositoryImpl implements FriendQueryRepository {
    private final JPAQueryFactory queryFactory;

    //메인화면: 친구 목록 조회
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

    @Override
    public List<FriendPageInfo> findFriendPage(FriendCondition condition) {
        return queryFactory
                .select(new QFriendPageInfo(friend1.id, friend1.friend.nickname, friend1.friend.picturePath))
                .from(friend1)
                .innerJoin(user)
                .on(friend1.user.eq(user))
                .where(
                        friendIdLt(condition.getOffset()),
                        userEq(condition.getUser()),
                        statusEq(condition.getStatus())
                )
                .orderBy(friend1.id.desc())
                .limit(condition.getSize() + 1)
                .fetch();
    }

    @Override
    public List<FriendPageInfo> findWaitFriendPage(FriendCondition condition) {
        return queryFactory
                .select(new QFriendPageInfo(friend1.id, friend1.user.nickname, friend1.user.picturePath))
                .from(friend1)
                .innerJoin(user)
                .on(friend1.user.eq(user))
                .where(
                        friendIdLt(condition.getOffset()),
                        friendEq(condition.getUser()),
                        statusEq(condition.getStatus())
                )
                .orderBy(friend1.id.asc())
                .limit(condition.getSize() + 1)
                .fetch();
    }

    @Override
    public List<FriendRecommendPageInfo> recommendFriends(String nickname, Location location, Double distance, int size) {
        return queryFactory
                .select(new QFriendRecommendPageInfo(
                        Expressions.stringTemplate("function('ST_DISTANCE_SPHERE', {0}, {1}, {2}, {3})",
                                        location.getLongitude(), location.getLatitude(), user.longitude, user.latitude)
                                .castToNum(Double.class)
                                .as("distance"),
                        user.nickname,
                        user.picturePath)
                )
                .from(user)
                .innerJoin(userProperty)
                .on(user.property.eq(userProperty))
                .where(
                        distanceGoe(location, distance),
                        userProperty.isExceptRecommend.eq(false),
                        user.nickname.ne(nickname)
                )
                .orderBy(Expressions.stringTemplate("function('ST_DISTANCE_SPHERE', {0}, {1}, {2}, {3})",
                                        location.getLongitude(), location.getLatitude(),
                                        user.longitude, user.latitude).asc(),
                        user.nickname.asc()
                )
                .limit(size + 1)
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

    private BooleanExpression distanceGoe(Location location, Double distance) {
        return distance != null ?
                Expressions.stringTemplate("function('ST_DISTANCE_SPHERE', {0}, {1}, {2}, {3})",
                                location.getLongitude(), location.getLatitude(),
                                user.longitude, user.latitude)
                        .castToNum(Double.class)
                        .goe(distance)
                :
                null;
    }

    private BooleanExpression friendIdLt(Long id) {
        return id != null ? friend1.id.lt(id) : null;
    }
}