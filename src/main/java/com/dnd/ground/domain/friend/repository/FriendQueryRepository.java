package com.dnd.ground.domain.friend.repository;

import com.dnd.ground.domain.friend.dto.FriendCondition;
import com.dnd.ground.domain.friend.dto.FriendPageInfo;
import com.dnd.ground.domain.friend.dto.FriendRecommendPageInfo;
import com.dnd.ground.domain.matrix.dto.Location;
import com.dnd.ground.domain.user.User;

import java.util.List;

/**
 * @description QueryDSL을 활용한 친구 조회 용 인터페이스
 * @author  박찬호
 * @since   2023.02.15
 * @updated 1. 네모두 추천 친구 쿼리 분리
 *          - 2023.05.25 박찬호
 */
public interface FriendQueryRepository {
    List<User> findFriends(FriendCondition condition);
    List<FriendPageInfo> findFriendPage(FriendCondition condition);
    List<FriendPageInfo> findWaitFriendPage(FriendCondition condition);
    List<FriendRecommendPageInfo> recommendFriends(Location location, Double offset, int size);
}
