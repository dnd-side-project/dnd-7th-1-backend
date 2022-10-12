package com.dnd.ground.domain.friend.service;

import com.dnd.ground.domain.friend.FriendStatus;
import com.dnd.ground.domain.friend.dto.FriendResponseDto;
import com.dnd.ground.domain.user.User;

import java.util.List;

/**
 * @description 친구와 관련된 서비스의 역할을 분리한 인터페이스
 * @author  박찬호
 * @since   2022-08-01
 * @updated 1.친구 요청 API 구현
 *          2.요청에 대한 응답 API 구현
 *          3.친구 삭제 API 구현
 *          - 2022.10.10 박찬호
 */

public interface FriendService {
    FriendResponseDto getFriends(String nickname); //친구 목록 조회용 DTO
    List<User> getFriends(User user); // 친구 목록 조회
    Boolean requestFriend(String userNickname, String friendNickname);

    FriendResponseDto.ResponseResult responseFriend(String userNickname, String friendNickname, FriendStatus status);

    Boolean deleteFriend(String userNickname, String friendNickname);

//    Boolean requestFriends(User user, List<User> friends);

//    List<User> getChallenge(User user); // 챌린지 진행중인 친구 목록 조회
//    List<User> getNotChallenge(User user); //챌린지를 진행중이지 않은 친구 목록 조회
}
