package com.dnd.ground.domain.friend.vo;

/**
 * @description 친구 검색 결과를 매핑하기 위한 Value Object
 * @author  박찬호
 * @since   2023-05-16
 * @updated 1.nickname, picturePath를 위한 VO 생성
 *          - 2023.05.16 박찬호
 */

public interface FriendSearchVo {
    String getNickname();
    String getPicturePath();
}
