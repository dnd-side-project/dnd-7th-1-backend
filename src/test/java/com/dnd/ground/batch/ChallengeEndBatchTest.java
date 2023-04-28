package com.dnd.ground.batch;

import com.dnd.ground.domain.challenge.Challenge;
import com.dnd.ground.domain.challenge.ChallengeStatus;
import com.dnd.ground.domain.challenge.UserChallenge;
import com.dnd.ground.domain.challenge.repository.ChallengeRepository;
import com.dnd.ground.domain.challenge.repository.UserChallengeRepository;
import com.dnd.ground.global.batch.challenge.ChallengeEndBatch;
import com.dnd.ground.global.batch.challenge.ChallengeWithUCDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBatchTest
@SpringBootTest(classes = {ChallengeEndBatch.class, BatchTestConfig.class, ChallengeRepository.class, UserChallengeRepository.class})
@ActiveProfiles("local")
public class ChallengeEndBatchTest {

    @Autowired
    @Qualifier("challenge_end_job")
    Job challenge_end_job;

    @Autowired
    JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    ChallengeRepository challengeRepository;

    @Autowired
    UserChallengeRepository userChallengeRepository;

    @Test
    @DisplayName("배치: 주간 챌린지 종료")
    void challengeEndBatch() throws Exception {
        System.out.println("---- TEST START ----");
        //GIVEN
        List<ChallengeWithUCDto> beforeList = new ArrayList<>();
        //테스트의 경우, 실행된 시점을 기준으로 진행 중 챌린지 종료 처리
        List<Challenge> challenges = challengeRepository.findWaitChallenge(LocalDateTime.of(LocalDate.now(), LocalTime.MIN), ChallengeStatus.PROGRESS);

        for (Challenge challenge : challenges) {
            List<UserChallenge> ucs = userChallengeRepository.findWaitUCs(challenge);
            beforeList.add(new ChallengeWithUCDto(challenge, ucs));
        }

        jobLauncherTestUtils.setJob(challenge_end_job);

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
                //멤버가 2명 이상인 경우, 챌린지 종료 처리

                //MASTER_PROGRESS, PROGRESS 상태 개수
                long readyCnt = beforeUCList.stream()
                        .filter((uc) -> uc.getStatus() == ChallengeStatus.MASTER_PROGRESS || uc.getStatus() == ChallengeStatus.PROGRESS)
                        .count();

                if (readyCnt < 2) {
                    //취소된 챌린지가 존재하는 경우
                    Optional<Challenge> afterChallengeOpt = challengeRepository.findByUuid(beforeChallenge.getUuid());
                    Assertions.assertTrue(afterChallengeOpt.isEmpty());

                    for (UserChallenge beforeUC : beforeUCList) {
                        Optional<UserChallenge> afterUCOpt = userChallengeRepository.findById(beforeUC.getId());
                        Assertions.assertTrue(afterUCOpt.isEmpty());
                    }
                } else {
                    //정상적으로 완료된 경우
                    Optional<Challenge> afterChallengeOpt = challengeRepository.findByUuid(beforeChallenge.getUuid());
                    Assertions.assertDoesNotThrow(afterChallengeOpt::get);

                    Challenge afterChallenge = afterChallengeOpt.get();
                    Assertions.assertEquals(afterChallenge.getStatus(), ChallengeStatus.DONE);

                    for (UserChallenge beforeUC : beforeUCList) {
                        Optional<UserChallenge> afterUCOpt = userChallengeRepository.findById(beforeUC.getId());
                        if (beforeUC.getStatus() == ChallengeStatus.MASTER_PROGRESS) {
                            Assertions.assertDoesNotThrow(afterUCOpt::get);
                            Assertions.assertEquals(afterUCOpt.get().getStatus(), ChallengeStatus.MASTER_DONE);
                        } else if (beforeUC.getStatus() == ChallengeStatus.PROGRESS) {
                            Assertions.assertDoesNotThrow(afterUCOpt::get);
                            Assertions.assertEquals(afterUCOpt.get().getStatus(), ChallengeStatus.DONE);
                        } else {
                            //WAIT, REJECT -> 삭제 처리
                            Assertions.assertTrue(afterUCOpt.isEmpty());
                        }
                    }
                }
            }
        }
    }
}
