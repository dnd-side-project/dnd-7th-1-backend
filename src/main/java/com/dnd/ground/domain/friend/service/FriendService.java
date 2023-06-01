package com.dnd.ground.domain.friend.service;

import com.dnd.ground.domain.friend.FriendStatus;
import com.dnd.ground.domain.friend.dto.FriendResponseDto;
import com.dnd.ground.domain.matrix.dto.Location;
import com.dnd.ground.domain.user.User;

import java.util.List;

/**
 * @description 친구와 관련된 서비스의 역할을 분리한 인터페이스
 * @author  박찬호
 * @since   2022-08-01
 * @updated 1.친구 삭제 벌크 API 구현
 *          - 2023.05.17 박찬호
 */

public interface FriendService {
    FriendResponseDto getFriends(String nickname, Long offset, Integer size);
    List<User> getFriends(User user);
    Boolean requestFriend(String userNickname, String friendNickname);
    FriendResponseDto.ResponseResult responseFriend(String receiverNickname, String senderNickname, FriendStatus status);
    Boolean deleteFriend(String userNickname, String friendNickname);
    void deleteFriendAll(User user);
    FriendStatus getFriendStatus(User user, User friend);
    FriendResponseDto getReceiveRequest(String nickname, Long offset, Integer size);
    FriendResponseDto.RecommendResponse recommendNemoduFriends(String nickname, Location location, Double offset, Integer size);
    List<FriendResponseDto.FInfo> searchFriend(String nickname, String keyword);
    Boolean deleteFriends(String nickname, List<String> friends);
}
