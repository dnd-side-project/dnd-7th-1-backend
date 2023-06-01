package com.dnd.ground.global.auth.vo;

import com.dnd.ground.domain.user.User;

/**
 * @description 조회된 카카오 친구 중, 네모두 회원을 조회하기 위한 VO
 * @author  박찬호
 * @since   2023-05-18
 * @updated 1.User, socialId로 회원을 조회하기 위한 VO 생성
 *          - 2023.05.18 박찬호
 */

public interface KakaoFriendVo {
    User getUser();
    Long getSocialId();
}
