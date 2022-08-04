package com.dnd.ground.domain.challenge.service;

import com.dnd.ground.domain.challenge.Challenge;
import com.dnd.ground.domain.challenge.UserChallenge;
import com.dnd.ground.domain.challenge.dto.ChallengeCreateRequestDto;
import com.dnd.ground.domain.challenge.repository.ChallengeRepository;
import com.dnd.ground.domain.challenge.repository.UserChallengeRepository;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.repository.UserRepository;
import com.dnd.ground.global.util.UuidUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @description 챌린지와 관련된 서비스의 역할을 분리한 구현체
 * @author  박찬호
 * @since   2022-08-03
 * @updated 1. UUID 추가
 *          - 2022.08.04 박찬호
 */

@RequiredArgsConstructor
@Service
public class ChallengeServiceImpl implements ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final UserChallengeRepository userChallengeRepository;
    private final UserRepository userRepository;

    @Transactional
    public ResponseEntity<?> createChallenge(ChallengeCreateRequestDto requestDto) {

        Challenge challenge = Challenge.create()
                .uuid(UuidUtil.createUUID())
                .name(requestDto.getName())
                .started(requestDto.getStarted())
                .message(requestDto.getMessage()) //메시지 처리 방식 결과에 따라 수정 요망
                .color(requestDto.getColor())
                .build();

        challengeRepository.save(challenge);

        for (String nickname : requestDto.getNicknames()) {
            User user = userRepository.findByNickName(nickname).orElseThrow(); //예외 처리 예정
            userChallengeRepository.save(new UserChallenge(challenge, user));
        }

        return new ResponseEntity(HttpStatus.CREATED);
    }
}
