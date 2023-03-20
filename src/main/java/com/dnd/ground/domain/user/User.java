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
 * @author  박찬호
 * @since   2022.07.28
 * @updated 1.User - UserProperty 분리
 *          2.메인화면 관련 필터 필드 및 메소드 삭제
 *           - 2023-03-20 박찬호
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

    @Column(name = "nickname", nullable = false, unique = true)
    private String nickname;

    @Email
    @Column(name="email", unique = true, nullable = false)
    private String email;

    @Column(name="intro")
    private String intro;

    @Column(name = "user_latitude")
    private Double latitude;

    @Column(name = "user_longitude")
    private Double longitude;

    @Column(nullable = false)
    private LocalDateTime created;

    @Column(name="picture_name", nullable = false)
    private String pictureName;

    @Column(name="picture_path", nullable = false)
    private String picturePath;

    @Enumerated(EnumType.STRING)
    @Column(name="login_type")
    private LoginType loginType;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "property_id")
    private UserProperty property;

    @OneToMany(mappedBy = "friend", cascade = CascadeType.ALL)
    private List<Friend> friends = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserChallenge> challenges = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<ExerciseRecord> exerciseRecords = new ArrayList<>();

    //마지막 위치 최신화
    public void updatePosition(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    //프로필 수정
    public void updateProfile(String nickname, String intro, String pictureName, String picturePath) {
        this.nickname = nickname;
        this.intro = intro;
        this.pictureName = pictureName;
        this.picturePath = picturePath;
    }

    public void setUserProperty(UserProperty property) {
        this.property = property;
        property.setUser(this);
    }
}