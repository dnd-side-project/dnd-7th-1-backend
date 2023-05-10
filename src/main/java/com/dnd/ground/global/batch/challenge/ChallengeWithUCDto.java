package com.dnd.ground.global.batch.challenge;

import com.dnd.ground.domain.challenge.Challenge;
import com.dnd.ground.domain.challenge.UserChallenge;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * @description 챌린지에 포함된 UC를 갖고 있는 DTO
 * @author  박찬호
 * @since   2023-04-14
 * @updated 1.DTO 생성
 *          - 2023-04-14 박찬호
 */

@Getter
@AllArgsConstructor
public class ChallengeWithUCDto {
    private Challenge challenge;
    private List<UserChallenge> ucs;
}
