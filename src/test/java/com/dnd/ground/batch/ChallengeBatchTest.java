package com.dnd.ground.batch;

import com.dnd.ground.GroundApplication;
import com.dnd.ground.domain.challenge.Challenge;
import com.dnd.ground.domain.challenge.ChallengeStatus;
import com.dnd.ground.domain.challenge.UserChallenge;
import com.dnd.ground.domain.challenge.repository.ChallengeRepository;
import com.dnd.ground.domain.challenge.repository.UserChallengeRepository;
import com.dnd.ground.global.batch.challenge.ChallengeStartBatch;
import com.dnd.ground.global.batch.challenge.ChallengeWithUCDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 박찬호
 * @description 주간 챌린지 시작 배치 TEST
 * @since 2023-04-27
 * @updated 1.테스트 실행
 *          2023-04-27 박찬호
 */

@RunWith(SpringRunner.class)
@SpringBatchTest
@SpringBootTest
@ContextConfiguration(classes = {GroundApplication.class, ChallengeStartBatch.class, BatchTestConfig.class})
@ActiveProfiles("local")
public class ChallengeBatchTest {
    @Autowired
    JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    ChallengeRepository challengeRepository;

    @Autowired
    UserChallengeRepository userChallengeRepository;


    @Test
    @DisplayName("배치: 주간 챌린지 시작")
    void challengeStartBatch() throws Exception {
        System.out.println("---- TEST START ----");

        //GIVEN
        List<ChallengeWithUCDto> beforeList = new ArrayList<>();
        List<Challenge> challenges = challengeRepository.findWaitChallenge(LocalDateTime.of(LocalDate.now(), LocalTime.MIN), ChallengeStatus.WAIT);

        for (Challenge challenge : challenges) {
            List<UserChallenge> ucs = userChallengeRepository.findWaitUCs(challenge);
            beforeList.add(new ChallengeWithUCDto(challenge, ucs));
        }

        //WHEN
        System.out.println("---- START BATCH ----");
        LocalDateTime jobParam = LocalDateTime.of(LocalDate.now(), LocalTime.now());
        JobExecution batchJob = jobLauncherTestUtils.launchJob(new JobParameters(
                Collections.singletonMap("requestDate", new JobParameter(String.valueOf(jobParam)))));
        System.out.println("---- END BATCH ----");
        //THEN
        Assertions.assertEquals(batchJob.getStatus(), BatchStatus.COMPLETED);

        for (ChallengeWithUCDto dto : beforeList) {
            Challenge beforeChallenge = dto.getChallenge();
            List<UserChallenge> beforeUCList = dto.getUcs();

            if (beforeUCList.size() < 2) {
                //기존 멤버가 0,1명인 경우 삭제 처리
                Optional<Challenge> after = challengeRepository.findByUuid(beforeChallenge.getUuid());
                Assertions.assertTrue(after.isEmpty());

                List<Long> beforeUCIds = beforeUCList.stream()
                        .map(UserChallenge::getId)
                        .collect(Collectors.toList());

                List<UserChallenge> afterUCs = userChallengeRepository.findAllById(beforeUCIds);
                Assertions.assertEquals(afterUCs.size(), 0);
            } else {
                /**
                 * 멤버가 2명 이상인 경우
                 * 1.정상적으로 챌린지 진행
                 * 2.거절, 대기 상태가 많아 1명만 남은 경우 삭제
                 */
                //MASTER, READY 상태 개수
                long readyCnt = beforeUCList.stream()
                        .filter((uc) -> uc.getStatus() == ChallengeStatus.MASTER || uc.getStatus() == ChallengeStatus.READY)
                        .count();

                if (readyCnt < 2) {
                    //챌린지가 취소된 경우
                    Optional<Challenge> afterChallengeOpt = challengeRepository.findByUuid(beforeChallenge.getUuid());
                    Assertions.assertTrue(afterChallengeOpt.isEmpty());

                    for (UserChallenge beforeUC : beforeUCList) {
                        Optional<UserChallenge> afterUCOpt = userChallengeRepository.findById(beforeUC.getId());
                        Assertions.assertTrue(afterUCOpt.isEmpty());
                    }
                } else {
                    //정상적으로 진행하는 경우
                    Optional<Challenge> afterChallengeOpt = challengeRepository.findByUuid(beforeChallenge.getUuid());
                    Assertions.assertDoesNotThrow(afterChallengeOpt::get);

                    Challenge afterChallenge = afterChallengeOpt.get();
                    Assertions.assertEquals(afterChallenge.getStatus(), ChallengeStatus.PROGRESS);

                    for (UserChallenge beforeUC : beforeUCList) {
                        Optional<UserChallenge> afterUCOpt = userChallengeRepository.findById(beforeUC.getId());
                        if (beforeUC.getStatus() == ChallengeStatus.MASTER) {
                            Assertions.assertDoesNotThrow(afterUCOpt::get);
                            Assertions.assertEquals(afterUCOpt.get().getStatus(), ChallengeStatus.MASTER_PROGRESS);
                        } else if (beforeUC.getStatus() == ChallengeStatus.READY) {
                            Assertions.assertDoesNotThrow(afterUCOpt::get);
                            Assertions.assertEquals(afterUCOpt.get().getStatus(), ChallengeStatus.PROGRESS);
                        } else {
                            //WAIT, REJECT -> 삭제 처리
                            Assertions.assertTrue(afterUCOpt.isEmpty());
                        }인
                    }
                }
            }
        }
    }
}
