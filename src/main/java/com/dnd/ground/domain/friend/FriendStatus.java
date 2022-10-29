package com.dnd.ground.domain.friend;

/**
 * @description A가 B에게 친구 요청을 하면, user에 A가, friend에 B가 저장됨.
 *              FriendStatus는 해당 친구 관계에 대한 상태만을 나타냄.
 *
 *              친구 상태
 *              Wait    - 대기
 *              Accept  - 수락
 *              Reject  - 거절
 *
 *              친구 요청중, 수락 대기중과 같이 클라이언트에서 필요한 다양한 상황을 정의해놓고, 클라이언트에게 전달할 때 사용한다.
 *              RequestWait  - 요청 대기중
 *              ResponseWait - 수락 대기중
 *              NoFriend     - 친구 아님
 *
 * @author  박찬호
 * @since   2022-07-28
 * @updated 2022-07-28 / 생성
 */

public enum FriendStatus {
    Wait, Accept, Reject,

    Requesting, ResponseWait, NoFriend
}
