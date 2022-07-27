package com.dnd.ground.domain.user;

import com.dnd.ground.domain.exerciseRecord.ExerciseRecord;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @description 회원 엔티티
 * @author  박찬호, 박세헌
 * @since   2022-07-26
 * @updated 2022-07-27 / 연관 관계 매핑 :박세헌
 */

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="user")
@Entity
public class User {

    @Id @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    @OneToMany(mappedBy = "user")
    private List<ExerciseRecord> exerciseRecords = new ArrayList<>();

    @Column(name = "username", nullable = false)
    private String userName;

    @Column(name = "nickname", nullable = false)
    private String nickName;

    @Column
    private String mail;

    @Column
    private String gender;

    @Column
    private double height;

    @Column
    private double weight;

}
