package com.dnd.ground.domain.challenge.service;

import com.dnd.ground.domain.challenge.Challenge;
import com.dnd.ground.domain.challenge.ChallengeStatus;
import com.dnd.ground.domain.challenge.UserChallenge;
import com.dnd.ground.domain.challenge.repository.ChallengeRepository;
import com.dnd.ground.domain.challenge.repository.UserChallengeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 박찬호
 * @description 챌린지 일괄 상태 변경
 * @since 2023-02-18
 * @updated 1.챌린지 상태 변경 메소드 이동
 *          2023-02-18 박찬호
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ChallengeBatch {
    private final ChallengeRepository challengeRepository;
    private final UserChallengeRepository userChallengeRepository;

    /*챌린지 상태 변경(매일 00:00 실행)*/
    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void startPeriodChallenge() {
        int countDelete = 0; //삭제된 챌린지 수
        int countUser = 0; //삭제된 유저 수
        int countProgress = 0; // 진행 상태로 바뀐 챌린지 수

        //챌린지 시작일이 오늘인 챌린지 리스트
        List<Challenge> challenges = challengeRepository.findChallengesNotStarted(LocalDate.now());

        for (Challenge challenge : challenges) {
            //Wait, Reject 상태의 유저 삭제
            countUser += userChallengeRepository.deleteUCByChallenge(challenge);

            List<UserChallenge> userChallenges = userChallengeRepository.findUCByChallenge(challenge);

            //주최자만 남은 경우 챌린지와 주최자 삭제
            if (userChallenges.size() == 1) {
                userChallengeRepository.delete(userChallenges.get(0));
                challengeRepository.delete(challenge);
                countDelete++;
            }
            //챌린지 진행 상태로 변경
            else {
                for (UserChallenge userChallenge : userChallenges) {
                    if (userChallenge.getStatus() == ChallengeStatus.MASTER)
                        userChallenge.changeStatus(ChallengeStatus.MASTER_PROGRESS);
                    else
                        userChallenge.changeStatus(ChallengeStatus.PROGRESS);
                }
                challenge.updateStatus(ChallengeStatus.PROGRESS);
            }
        }

        log.info("**챌린지 시작 메소드 실행** 현재 시간:{} | 삭제된 챌린지 개수:{} | 삭제된 유저 수:{} | 진행 상태로 바뀐 챌린지 개수:{}",
                LocalDateTime.now(), countDelete, countUser, countProgress);
    }

    /*일주일 챌린지 마감(매주 일요일 오후 11시 59분 50초 실행)*/
    @Transactional
    @Scheduled(cron = "50 59 23 * * 0")
    public void endPeriodChallenge() {
        //진행 중인 챌린지 모두 조회
        List<Challenge> challenges = challengeRepository.findChallengesByStatusEquals(ChallengeStatus.PROGRESS);

        for (Challenge challenge : challenges) {
            //챌린지 완료
            challenge.updateStatus(ChallengeStatus.DONE);

            //각 유저들도 완료 상태 변경
            List<UserChallenge> userChallenges = userChallengeRepository.findUCByChallenge(challenge);

            for (UserChallenge userChallenge : userChallenges) {
                if (userChallenge.getStatus() == ChallengeStatus.MASTER_PROGRESS)
                    userChallenge.changeStatus(ChallengeStatus.MASTER_DONE);
                else
                    userChallenge.changeStatus(ChallengeStatus.DONE);
            }
        }

        log.info("**챌린지 종료 메소드 실행** 현재 시간:{} | 종료된 챌린지 개수:{}", LocalDateTime.now(), challenges.size());
    }
}
