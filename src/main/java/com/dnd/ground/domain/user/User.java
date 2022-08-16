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
 * @updated 1. 회원 생성 시간 필드 추가
 *          -2022.08.16 박찬호
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
}