package com.dnd.ground.domain.exerciseRecord.dto;

import com.dnd.ground.domain.challenge.ChallengeType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @description QueryDSL에서 받아올 랭킹 관련 DTO
 * @author  박찬호
 * @since   2023-02-19
 * @updated 1.그룹화를 위해 챌린지 타입 필드 추가
 *          - 2023-03-03 박찬호
 */

@AllArgsConstructor
@Setter
@Getter
public class RankDto implements Comparable<RankDto> {
    private String nickname;
    private String picturePath;
    private ChallengeType type;
    private Long score;

    public RankDto(String nickname, String picturePath, Long score) {
        this.nickname = nickname;
        this.picturePath = picturePath;
        this.score = score;
    }

    @Override
    public int compareTo(RankDto o) {
        return (int) (o.getScore() - this.score);
    }
}
