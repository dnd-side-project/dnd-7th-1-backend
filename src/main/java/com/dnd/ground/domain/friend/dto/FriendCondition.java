package com.dnd.ground.domain.friend.dto;

import com.dnd.ground.domain.friend.FriendStatus;
import com.dnd.ground.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @description QueryDSL을 활용한 친구 조회 용 검색 조건
 * @author  박찬호
 * @since   2023.02.15
 * @updated 1. 클래스 생성
 *          - 2023.02.15 박찬호
 */

@Getter
@Setter
@AllArgsConstructor
@Builder
public class FriendCondition {
    private User user;
    private User friend;
    private FriendStatus status;
}
