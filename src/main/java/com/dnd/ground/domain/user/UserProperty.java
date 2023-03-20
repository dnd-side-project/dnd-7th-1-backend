package com.dnd.ground.domain.user;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @description 회원 정보 엔티티
 * @author  박찬호
 * @since   2023.03.20
 * @updated 1.User - UserProperty 분리
 *          2.필터, 동의 여부, 토큰 정보 등 부가적인 회원 정보 저장
 *           - 2023-03-20 박찬호
 */

@Entity
@Table(name="user_property")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class UserProperty {
    @Id @GeneratedValue
    @Column(name = "property_id")
    private Long id;

    @OneToOne(mappedBy = "property", fetch = FetchType.LAZY)
    private User user;

    @Column(name = "fcm_token", nullable = false, unique = true)
    private String fcmToken;

    @Column(name = "fcm_token_updated", nullable = false)
    private LocalDateTime fcmTokenUpdated;

    @Column(name = "social_id", unique = true)
    private String socialId;

    /**
     * 메인 화면 필터
     */
    @Column(name="is_show_mine", nullable = false)
    private Boolean isShowMine;

    @Column(name="is_show_friend", nullable = false)
    private Boolean isShowFriend;

    @Column(name="is_public_record", nullable = false)
    private Boolean isPublicRecord;

    /**
     * 푸시 알람
     */
    /*공통*/
    @Column(name="noti_week_start", nullable = false)
    private Boolean notiWeekStart;

    @Column(name="noti_week_end", nullable = false)
    private Boolean notiWeekEnd;

    /*친구*/
    @Column(name="noti_friend_request", nullable = false)
    private Boolean notiFriendRequest;

    @Column(name="noti_friend_accept", nullable = false)
    private Boolean notiFriendAccept;

    /*챌린지*/
    @Column(name="noti_challenge_request", nullable = false)
    private Boolean notiChallengeRequest;

    @Column(name="noti_challenge_start", nullable = false)
    private Boolean notiChallengeStart;

    @Column(name="noti_challenge_cancel", nullable = false)
    private Boolean notiChallengeCancel;

    @Column(name="noti_challenge_result", nullable = false)
    private Boolean notiChallengeResult;

    public void setUser(User user) {
        this.user = user;
    }

    //"나의 기록 보기" 필터 변경
    public Boolean changeFilterMine() {
        this.isShowMine = !this.isShowMine;
        return this.isShowMine;
    }

    //"친구 보기" 필터 변경
    public Boolean changeFilterFriend() {
        this.isShowFriend = !this.isShowFriend;
        return this.isShowFriend;
    }

    //"친구들에게 보이기" 필터 변경
    public Boolean changeFilterRecord() {
        this.isPublicRecord = !this.isPublicRecord;
        return this.isPublicRecord;
    }
}
