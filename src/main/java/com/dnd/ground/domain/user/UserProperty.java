package com.dnd.ground.domain.user;

import com.dnd.ground.global.exception.ExceptionCodeSet;
import com.dnd.ground.global.exception.UserException;
import com.dnd.ground.global.notification.NotificationMessage;
import lombok.*;

import javax.persistence.*;

/**
 * @description 회원 정보 엔티티
 * @author  박찬호
 * @since   2023.03.20
 * @updated 1.친구 추천 목록 제외 필터 변경 API 구현
 *          - 2023-05-23 박찬호
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

    @Column(name = "social_id", unique = true)
    private String socialId;

    @Column(name = "is_except_recommend", nullable = false)
    private Boolean isExceptRecommend;

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

    @Column(name="noti_challenge_accept", nullable = false)
    private Boolean notiChallengeAccept;

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

    //'친구 추천 목록 제외' 필터 변경
    public Boolean changeFilterExceptRecommend() {
        this.isExceptRecommend = !this.isExceptRecommend;
        return this.isExceptRecommend;
    }

    public Boolean changeFilterNotification(NotificationMessage message) {
        switch (message) {
            case COMMON_WEEK_START:
                return this.notiWeekStart = !this.notiWeekStart;
            case COMMON_WEEK_END:
                return this.notiWeekEnd = !this.notiWeekEnd;
            case FRIEND_RECEIVED_REQUEST:
                return this.notiFriendRequest = !this.notiFriendRequest;
            case FRIEND_ACCEPT:
                return this.notiFriendAccept = !this.notiFriendAccept;
            case CHALLENGE_RECEIVED_REQUEST:
                return this.notiChallengeRequest = !this.notiChallengeRequest;
            case CHALLENGE_ACCEPTED:
                return this.notiChallengeAccept = !this.notiChallengeAccept;
            case CHALLENGE_START_SOON:
                return this.notiChallengeStart = !this.notiChallengeStart;
            case CHALLENGE_CANCELED:
                return this.notiChallengeCancel = !this.notiChallengeCancel;
            case CHALLENGE_RESULT:
                return this.notiChallengeResult = !this.notiChallengeResult;
            default:
                throw new UserException(ExceptionCodeSet.NOTI_INVALID_MSG);
        }
    }

    public boolean checkNotiFilter(NotificationMessage message) {
        switch (message) {
            case COMMON_WEEK_START:
                return this.notiWeekStart;
            case COMMON_WEEK_END:
                return this.notiWeekEnd;
            case FRIEND_RECEIVED_REQUEST:
                return this.notiFriendRequest;
            case FRIEND_ACCEPT:
                return this.notiFriendAccept;
            case CHALLENGE_RECEIVED_REQUEST:
                return this.notiChallengeRequest;
            case CHALLENGE_ACCEPTED:
                return this.notiChallengeAccept;
            case CHALLENGE_START_SOON:
                return this.notiChallengeStart;
            case CHALLENGE_CANCELED:
                return this.notiChallengeCancel;
            case CHALLENGE_RESULT:
                return this.notiChallengeResult;
            default:
                throw new UserException(ExceptionCodeSet.NOTI_INVALID_MSG);
        }
    }
}
