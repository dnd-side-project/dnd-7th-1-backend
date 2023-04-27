package com.dnd.ground.global.batch.challenge;

import com.dnd.ground.domain.challenge.Challenge;
import com.dnd.ground.domain.challenge.ChallengeStatus;
import com.dnd.ground.domain.challenge.UserChallenge;
import com.dnd.ground.domain.challenge.repository.ChallengeRepository;
import com.dnd.ground.domain.challenge.repository.UserChallengeRepository;
import com.dnd.ground.global.batch.*;
import com.dnd.ground.global.log.CommonLogger;
import com.dnd.ground.global.notification.NotificationForm;
import com.dnd.ground.global.notification.NotificationMessage;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @description 주간 챌린지 시작 배치
 * @author  박찬호
 * @since   2023-04-20
 * @updated 1.배치 작업 정의
 *          - 2023-04-27 박찬호
 */

@Configuration
public class ChallengeStartBatch {
    public ChallengeStartBatch(JobBuilderFactory jobBuilderFactory,
                               StepBuilderFactory stepBuilderFactory,
                               JobParamDateTimeConverter dateTimeConverter,
                               ChallengeRepository challengeRepository,
                               UserChallengeRepository userChallengeRepository,
                               ApplicationEventPublisher applicationEventPublisher,
                               EntityManagerFactory entityManagerFactory,
                               @Qualifier("batchLogger") CommonLogger logger) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.dateTimeConverter = dateTimeConverter;
        this.challengeRepository = challengeRepository;
        this.userChallengeRepository = userChallengeRepository;
        this.pushNotificationPublisher = applicationEventPublisher;
        this.emf = entityManagerFactory;
        this.logger = logger;
    }

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final JobParamDateTimeConverter dateTimeConverter;
    private final ChallengeRepository challengeRepository;
    private final UserChallengeRepository userChallengeRepository;
    private final ApplicationEventPublisher pushNotificationPublisher;
    private final EntityManagerFactory emf;
    private final CommonLogger logger;
    private static final String JOB_NAME = "challenge_start";

    @Bean(name = JOB_NAME + "_job")
    public Job challengeStartJob(Step challengeStartStep) {
        return jobBuilderFactory.get(JOB_NAME + "_job")
                .start(challengeStartStep)
                .build();
    }

    @JobScope
    @Bean(name = JOB_NAME + "_job_param_converter")
    public JobParamDateTimeConverter localDateTimeConverter(@Value("#{jobParameters[requestDate]}") String createdStr) {
        return new JobParamDateTimeConverter(createdStr);
    }

    @JobScope
    @Bean(name = JOB_NAME + "_step")
    public Step challengeStartStep(ItemProcessor<ChallengeWithUCDto, ChallengeWithUCDto> challengeStartProcessor,
                                   ChallengeWithUCItemWriter itemWriter) {
        //ItemReader 객체 생성
        Map<String, Object> challengeParam = new HashMap<>();
        challengeParam.put("jobParam", dateTimeConverter.getCreated());
        challengeParam.put("status", ChallengeStatus.WAIT);

        ChallengeWithUCItemReader reader = new ChallengeWithUCItemReader(challengeParam, emf, logger);

        return stepBuilderFactory.get(JOB_NAME + "_step")
                .<ChallengeWithUCDto, ChallengeWithUCDto>chunk(10)
                .reader(reader)
                .processor(challengeStartProcessor)
                .writer(itemWriter)
                .build();
    }

    @StepScope
    @Bean(name = JOB_NAME + "_processor")
    public ItemProcessor<ChallengeWithUCDto, ChallengeWithUCDto> challengeStartProcessor() {
        return item -> {
            Challenge challenge = item.getChallenge();
            List<UserChallenge> ucs = item.getUcs();

            if (ucs.size() == 0) {
                challengeRepository.delete(challenge);
            } else if (ucs.size() == 1) {
                sendCancelNoti(ucs.get(0)); //챌린지 Master에게 푸시 알람 전송
                userChallengeRepository.delete(ucs.get(0));
                challengeRepository.delete(challenge);
                return null;
            } else {
                challenge.updateStatus(ChallengeStatus.PROGRESS);
                List<UserChallenge> exceptMembers = new ArrayList<>();
                for (UserChallenge uc : ucs) {
                    if (uc.getStatus() == ChallengeStatus.MASTER) uc.changeStatus(ChallengeStatus.MASTER_PROGRESS);
                    else if (uc.getStatus() == ChallengeStatus.READY) uc.changeStatus(ChallengeStatus.PROGRESS);
                    else {
                        exceptMembers.add(uc);
                    }
                }

                userChallengeRepository.deleteAll(exceptMembers);
                ucs.removeAll(exceptMembers);
                if (ucs.size() < 2) {
                    if (ucs.size() == 1) sendCancelNoti(ucs.get(0));
                    userChallengeRepository.deleteAll(ucs);
                    challengeRepository.delete(challenge);
                    return null;
                }
            }
            return item;
        };
    }

    private void sendCancelNoti(UserChallenge uc) {
        LocalDateTime reserved = dateTimeConverter.getCreated().withHour(9);
        pushNotificationPublisher.publishEvent(
                new NotificationForm(List.of(uc.getUser()), NotificationMessage.CHALLENGE_CANCELED, reserved)
        );
    }
}
