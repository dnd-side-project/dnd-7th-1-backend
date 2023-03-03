package com.dnd.ground.domain.challenge.dto;

import com.dnd.ground.domain.challenge.Challenge;
import com.dnd.ground.domain.challenge.ChallengeColor;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

/**
 * @author 박찬호
 * @description 챌린지와 색깔 정보 조회용 DTO
 * @since 2023-02-15
 * @updated 1.클래스 생성
 *          - 2023.02.15 박찬호
 */

@Getter
@Setter
public class ChallengeColorDto {
    private Challenge challenge;
    private ChallengeColor color;

    @QueryProjection
    public ChallengeColorDto(Challenge challenge, ChallengeColor color) {
        this.challenge = challenge;
        this.color = color;
    }

    public ChallengeColor findColor(Challenge challenge) {
        if (this.challenge == challenge) return this.color;
        else return null;
    }
}
