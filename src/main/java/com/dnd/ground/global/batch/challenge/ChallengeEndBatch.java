package com.dnd.ground.global.batch.challenge;

import com.dnd.ground.domain.challenge.Challenge;
import com.dnd.ground.domain.challenge.ChallengeStatus;
import com.dnd.ground.domain.challenge.UserChallenge;
import com.dnd.ground.domain.challenge.repository.ChallengeRepository;
import com.dnd.ground.domain.challenge.repository.UserChallengeRepository;
import com.dnd.ground.global.batch.JobLoggerListener;
import com.dnd.ground.global.batch.JobParamDateTimeConverter;
import com.dnd.ground.global.log.CommonLogger;
import com.dnd.ground.global.util.UuidUtil;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description 주간 챌린지 종료 배치
 * @author  박찬호
 * @since   2023-04-28
 * @updated 1.Job 전/후 로그 리스너 추가
 *          - 2023-05-02 박찬호
 */

@Configuration
public class ChallengeEndBatch {
    public ChallengeEndBatch(JobBuilderFactory jobBuilderFactory,
                             StepBuilderFactory stepBuilderFactory,
                             JobParamDateTimeConverter challenge_end_job_param_converter,
                             JobLoggerListener jobLoggerListener,
                             EntityManagerFactory entityManagerFactory,
                             @Qualifier("batchLogger") CommonLogger logger,
                             ChallengeRepository challengeRepository,
                             UserChallengeRepository userChallengeRepository) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.dateTimeConverter = challenge_end_job_param_converter;
        this.jobLoggerListener = jobLoggerListener;
        this.emf = entityManagerFactory;
        this.logger = logger;
        this.challengeRepository = challengeRepository;
        this.userChallengeRepository = userChallengeRepository;
    }

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final JobParamDateTimeConverter dateTimeConverter;
    private final JobLoggerListener jobLoggerListener;

    private final EntityManagerFactory emf;
    private final CommonLogger logger;
    private final ChallengeRepository challengeRepository;
    private final UserChallengeRepository userChallengeRepository;

    private static final String JOB_NAME = "challenge_end";

    @Bean(name = JOB_NAME + "_job")
    @Qualifier("challenge_end_job")
    public Job challengeEndJob(Step challenge_end_step) {
        return jobBuilderFactory.get(JOB_NAME + "_job")
                .start(challenge_end_step)
                .listener(jobLoggerListener)
                .build();
    }

    @JobScope
    @Bean(name = JOB_NAME + "_job_param_converter")
    public JobParamDateTimeConverter localDateTimeConverter(@Value("#{jobParameters[requestDate]}") String createdStr) {
        return new JobParamDateTimeConverter(createdStr);
    }

    @JobScope
    @Bean(name = JOB_NAME + "_step")
    public Step challengeEndStep(ItemProcessor<ChallengeWithUCDto, ChallengeWithUCDto> challenge_end_processor,
                                 ChallengeWithUCItemWriter itemWriter) {
        //ItemReader 객체 생성
        Map<String, Object> challengeParam = new HashMap<>();
        challengeParam.put("jobParam", dateTimeConverter.getCreated());
        challengeParam.put("status", ChallengeStatus.PROGRESS);

        ChallengeWithUCItemReader reader = new ChallengeWithUCItemReader(challengeParam, emf, logger);

        return stepBuilderFactory.get(JOB_NAME + "_step")
                .<ChallengeWithUCDto, ChallengeWithUCDto>chunk(10)
                .reader(reader)
                .processor(challenge_end_processor)
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
                userChallengeRepository.delete(ucs.get(0));
                challengeRepository.delete(challenge);
                return null;
            } else {
                challenge.updateStatus(ChallengeStatus.DONE);
                List<UserChallenge> exceptMembers = new ArrayList<>();
                for (UserChallenge uc : ucs) {
                    if (uc.getStatus() == ChallengeStatus.MASTER_PROGRESS) uc.changeStatus(ChallengeStatus.MASTER_DONE);
                    else if (uc.getStatus() == ChallengeStatus.PROGRESS) uc.changeStatus(ChallengeStatus.DONE);
                    else {
                        logger.errorWrite(String.format("챌린지 종료 배치: 회원의 상태가 올바르지 않습니다: 닉네임:%s 상태:%s", uc.getUser().getNickname(), uc.getStatus().name()));
                        exceptMembers.add(uc);
                    }
                }

                userChallengeRepository.deleteAll(exceptMembers);
                ucs.removeAll(exceptMembers);
                if (ucs.size() < 2) {
                    logger.errorWrite(String.format("챌린지 종료 배치: 취소된 챌린지입니다: 이름:%s UUID:%s", challenge.getName(), UuidUtil.bytesToHex(challenge.getUuid())));
                    userChallengeRepository.deleteAll(ucs);
                    challengeRepository.delete(challenge);
                    return null;
                }
            }
            return item;
        };
    }
}
