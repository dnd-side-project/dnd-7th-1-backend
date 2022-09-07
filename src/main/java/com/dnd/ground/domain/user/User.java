package com.dnd.ground.domain.user;

import com.dnd.ground.domain.challenge.UserChallenge;
import com.dnd.ground.domain.exerciseRecord.ExerciseRecord;
import com.dnd.ground.domain.friend.Friend;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @description 회원 엔티티
 * @author  박찬호, 박세헌
 * @since   2022.07.28
 * @updated 1. 리프레시 토큰 필드 생성 및 비즈니스 로직 추가
 *  *          - 2022-09-02 박세헌
 */

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="user")
@Entity
public class User {

    @Id @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    @Column(name = "kakao_id")
    private Long kakaoId;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "nickname", nullable = false, unique = true)
    private String nickname;

    @Email
    @Column(unique = true)
    private String mail;

    @Column
    private String intro;

    @Column(name = "user_latitude")
    private Double latitude;

    @Column(name = "user_longitude")
    private Double longitude;

    @Column(nullable = false)
    private LocalDateTime created;

    @Column(name="is_show_mine", nullable = false)
    private Boolean isShowMine;

    @Column(name="is_show_friend", nullable = false)
    private Boolean isShowFriend;

    @Column(name="is_public_record", nullable = false)
    private Boolean isPublicRecord;

//    @Column(name="picture_name", nullable = false)
//    private String pictureName;
//
//    @Column(name="picture_path", nullable = false)
//    private String picturePath;

    @Column(name="refresh_token")
    private String refreshToken;

    @OneToMany(mappedBy = "friend")
    private List<Friend> friends = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserChallenge> challenges = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<ExerciseRecord> exerciseRecords = new ArrayList<>();

    //마지막 위치 최신화
    public void updatePosition(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    //필터 변경
    public Boolean changeFilterMine() {
        this.isShowMine = !this.isShowMine;
        return this.isShowMine;
    }

    public Boolean changeFilterFriend() {
        this.isShowFriend = !this.isShowFriend;
        return this.isShowFriend;
    }

    public Boolean changeFilterRecord() {
        this.isPublicRecord = !this.isPublicRecord;
        return this.isPublicRecord;
    }

    public void updateProfile(String nickname, String intro) {
        this.nickname = nickname;
        this.intro = intro;
    }

    public void updateRefreshToken(String refreshToken){
        this.refreshToken = refreshToken;
    }
}