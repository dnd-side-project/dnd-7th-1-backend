package com.dnd.ground.domain.friend.service;

import com.dnd.ground.domain.friend.FriendStatus;
import com.dnd.ground.domain.friend.dto.FriendResponseDto;
import com.dnd.ground.domain.user.User;

import java.util.List;

/**
 * @description 친구와 관련된 서비스의 역할을 분리한 인터페이스
 * @author  박찬호
 * @since   2022-08-01
 * @updated 1.친구 목록 조회 시, 페이징 적용에 따른 offset 파라미터 추가
 *          2.친구 요청 목록 조회 기능 구현
 *          - 2022.10.29 박찬호
 */

public interface FriendService {
    FriendResponseDto getFriends(String nickname, Integer offset); //친구 목록 조회용 DTO
    List<User> getFriends(User user); // 친구 목록 조회
    Boolean requestFriend(String userNickname, String friendNickname);

    FriendResponseDto.ResponseResult responseFriend(String userNickname, String friendNickname, FriendStatus status);

    Boolean deleteFriend(String userNickname, String friendNickname);
    FriendStatus getFriendStatus(User user, User friend);

    List<FriendResponseDto.FInfo> getReceiveRequest(String nickname);
}
