package com.dnd.ground.domain.friend.repository;

import com.dnd.ground.domain.friend.dto.FriendCondition;
import com.dnd.ground.domain.user.User;

import java.util.List;

/**
 * @description QueryDSL을 활용한 친구 조회 용 인터페이스
 * @author  박찬호
 * @since   2023.02.15
 * @updated 1. 인터페이스 생성 및 친구 조회 쿼리 생성
 *          - 2023.02.15 박찬호
 */
public interface FriendQueryRepository {
    List<User> findFriends(FriendCondition condition);
}
