package com.dnd.ground.domain.friend.service;

import com.dnd.ground.domain.friend.Friend;
import com.dnd.ground.domain.friend.FriendStatus;
import com.dnd.ground.domain.friend.dto.*;
import com.dnd.ground.domain.friend.repository.FriendRepository;
import com.dnd.ground.domain.friend.vo.FriendSearchVo;
import com.dnd.ground.domain.matrix.dto.Location;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.repository.UserRepository;
import com.dnd.ground.global.exception.ExceptionCodeSet;
import com.dnd.ground.global.exception.FriendException;
import com.dnd.ground.global.exception.UserException;
import com.dnd.ground.global.notification.dto.NotificationForm;
import com.dnd.ground.global.notification.NotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.dnd.ground.domain.friend.FriendStatus.*;

/**
 * @description 친구와 관련된 서비스 구현체
 * @author 박찬호
 * @since 2022-08-01
 * @updated 1.네모두 추천 친구 닉네임 유무에 따른 로직 추가
 *          2.친구 요청 응답 변수명 수정
 *          - 2023.05.27 박찬호
 */

@Slf4j
@RequiredArgsConstructor
@Service
public class FriendServiceImpl implements FriendService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher pushNotificationPublisher;

    //친구 목록과 추가 정보를 함께 반환
    public FriendResponseDto getFriends(String nickname, Long offset, Integer size) {
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));

        List<FriendPageInfo> result = friendRepository.findFriendPage(FriendCondition.builder()
                .user(user)
                .status(ACCEPT)
                .offset(offset)
                .size(size)
                .build()
        );

        boolean isLast = result.size() <= size;
        if (!isLast) result.remove(result.size() - 1);
        Long nextOffset = isLast ? null : result.get(result.size() - 1).getId();

        List<FriendResponseDto.FInfo> content = result.stream()
                .map(f -> new FriendResponseDto.FInfo(f.getNickname(), f.getPicturePath()))
                .collect(Collectors.toList());

        return new FriendResponseDto(content, result.size(), isLast, nextOffset);
    }

    //요청받은 친구 목록 조회
    public FriendResponseDto getReceiveRequest(String nickname, Long offset, Integer size) {
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));

        List<FriendPageInfo> result = friendRepository.findWaitFriendPage(FriendCondition.builder()
                .user(user)
                .status(WAIT)
                .offset(offset)
                .size(size)
                .build()
        );

        boolean isLast = result.size() <= size;
        if (!isLast) result.remove(result.size() - 1);
        Long nextOffset = isLast ? null : result.get(result.size() - 1).getId();

        List<FriendResponseDto.FInfo> content = result.stream()
                .map(f -> new FriendResponseDto.FInfo(f.getNickname(), f.getPicturePath()))
                .collect(Collectors.toList());

        return new FriendResponseDto(content, result.size(), isLast, nextOffset);
    }

    /*네모두 추천 친구(거리가 가까운 순으로 추천)*/
    @Override
    public FriendResponseDto.RecommendResponse recommendNemoduFriends(String nickname, Location location, Double distance, Integer size) {
        List<FriendRecommendPageInfo> result = friendRepository.recommendFriends(location, distance, size);
        Set<String> friends = new HashSet<>();

        if (nickname != null) {
            Optional<User> nicknameOpt = userRepository.findByNickname(nickname);
            if (nicknameOpt.isPresent()) {
                User user = nicknameOpt.get();
                friends.add(nickname);

                List<Friend> allFriendRelations = friendRepository.findAllFriends(user);
                for (Friend friend : allFriendRelations) {
                    if (friend.getFriend() == user) friends.add(friend.getUser().getNickname());
                    else if (friend.getUser() == user) friends.add(friend.getFriend().getNickname());
                }
            }
        }

        boolean isLast = result.size() <= size;
        if (!isLast) result.remove(result.size() - 1);
        Double offset = isLast ? null : result.get(result.size() - 1).getDistance();

        List<FriendResponseDto.FInfo> content = result.stream()
                .map(f -> new FriendResponseDto.FInfo(f.getNickname(), f.getPicturePath()))
                .filter(f -> !friends.contains(f.getNickname()))
                .collect(Collectors.toList());

        return new FriendResponseDto.RecommendResponse(content, content.size(), isLast, offset);
    }

    /*친구 검색*/
    @Override
    public List<FriendResponseDto.FInfo> searchFriend(String nickname, String keyword) {
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));

        List<FriendSearchVo> result = friendRepository.searchWithFullTextIdx(user.getId(), keyword);

        return result.stream()
                .map(r -> new FriendResponseDto.FInfo(r.getNickname(), r.getPicturePath()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Boolean deleteFriends(String nickname, List<String> friendNicknames) {
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));

        List<User> friends = userRepository.findAllByNickname(friendNicknames);
        if (friendNicknames.size() != friends.size()) throw new FriendException(ExceptionCodeSet.FRIEND_NOT_FOUND);

        int result = friendRepository.deleteBulk(user, friends);
        if (result / 2 == friends.size()) return true;
        else throw new FriendException(ExceptionCodeSet.FRIEND_FAIL_DELETE);
    }

    //List<User> 형태의 친구 목록 반환
    public List<User> getFriends(User user) {
        FriendCondition condition = FriendCondition.builder()
                .user(user)
                .status(ACCEPT)
                .build();

        return friendRepository.findFriends(condition);
    }

    /**
     * 단건 친구 요청 *
     * 누가 요청을 보냈는지 알기 위해 다음과 같은 명확한 기준을 세운다.
     * 추후 친구 요청 수락을 위해 요청을 보내는 쪽이 user, 받는 쪽이 friend에 저장한다.
     * 요청한 경우 1건만 저장되지만, 수락하게 되면 양 측에 저장되어 2건이 저장된다.
     *
     * @param userNickname
     * @param friendNickname
     * @return boolean
     */
    @Transactional
    public Boolean requestFriend(String userNickname, String friendNickname) {
        User user = userRepository.findByNickname(userNickname)
                .orElseThrow(() -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));

        User friend = userRepository.findByNicknameWithProperty(friendNickname)
                .orElseThrow(() -> new FriendException(ExceptionCodeSet.FRIEND_NOT_FOUND));

        if (friendRepository.findFriendInProgress(user, friend).size() > 0)
            throw new FriendException(ExceptionCodeSet.FRIEND_DUPL);
        else if (userNickname.equals(friendNickname)) throw new FriendException(ExceptionCodeSet.BAD_REQUEST);

        friendRepository.save(new Friend(user, friend, WAIT));

        pushNotificationPublisher.publishEvent(
                new NotificationForm(
                        new ArrayList<>(Arrays.asList(friend)),
                        List.of(user.getNickname()),
                        null,
                        NotificationMessage.FRIEND_RECEIVED_REQUEST)
        );

        return true;
    }

    /*친구 요청 수락, 거절 등에 대한 처리*/
    @Transactional
    public FriendResponseDto.ResponseResult responseFriend(String receiverNickname, String senderNickname, FriendStatus status) {
        User receiver = userRepository.findByNickname(receiverNickname)
                .orElseThrow(() -> new UserException(ExceptionCodeSet.USER_NOT_FOUND)
        );

        User sender = userRepository.findByNicknameWithProperty(senderNickname)
                .orElseThrow(() -> new FriendException(ExceptionCodeSet.FRIEND_NOT_FOUND)
        );

        Friend friendRelation = friendRepository.findRequestFriend(receiver, sender)
                .orElseThrow(() -> new FriendException(ExceptionCodeSet.FRIEND_NOT_FOUND_REQ)
        );

        if (status.equals(ACCEPT)) {
            friendRelation.updateStatus(ACCEPT);
            friendRepository.save(
                    Friend.builder()
                            .user(receiver)
                            .friend(sender)
                            .status(ACCEPT)
                            .build()
            );

            pushNotificationPublisher.publishEvent(new NotificationForm(
                    new ArrayList<>(Collections.singletonList(sender)),
                    List.of(receiver.getNickname()),
                    null,
                    NotificationMessage.FRIEND_ACCEPT)
            );
        } else if (status.equals(REJECT)) {
            friendRepository.delete(friendRelation);
        } else throw new FriendException(ExceptionCodeSet.FRIEND_INVALID_STATUS);

        return new FriendResponseDto.ResponseResult(sender.getNickname(), receiver.getNickname(), friendRelation.getStatus());
    }

    /*친구 삭제*/
    @Transactional
    public Boolean deleteFriend(String userNickname, String friendNickname) {
        User user = userRepository.findByNickname(userNickname)
                .orElseThrow(() -> new UserException(ExceptionCodeSet.USER_NOT_FOUND));

        User friend = userRepository.findByNickname(friendNickname)
                .orElseThrow(() -> new FriendException(ExceptionCodeSet.FRIEND_NOT_FOUND));

        List<Friend> friendRelation = friendRepository.findFriendRelation(user, friend);
        if (friendRelation.isEmpty()) throw new FriendException(ExceptionCodeSet.FRIEND_NOT_FOUND);
        else {
            friendRepository.deleteAll(friendRelation);
            return true;
        }
    }

    /*전체 친구 삭제*/
    public void deleteFriendAll(User user) {
        friendRepository.deleteAllByUser(user);
    }

    /*회원과 친구가 어떤 관계인지 나타내는 메소드*/
    public FriendStatus getFriendStatus(User user, User friend) {
        List<Friend> friendRelation = friendRepository.findFriendRelation(user, friend);
        FriendStatus isFriend = NO_FRIEND;

        if (friendRelation.size() == 2) {
            for (Friend f : friendRelation) {
                if (!f.getStatus().equals(ACCEPT))
                    return NO_FRIEND;
            }
            isFriend = ACCEPT;
        }
        else if (friendRelation.size() == 1) {
            Friend f = friendRelation.get(0);
            FriendStatus status = f.getStatus();
            if (status.equals(WAIT)) {
                if (f.getUser().equals(user)) isFriend = REQUESTING;
                else isFriend = RESPONSE_WAIT;
            }
        }

        return isFriend;
    }
}
