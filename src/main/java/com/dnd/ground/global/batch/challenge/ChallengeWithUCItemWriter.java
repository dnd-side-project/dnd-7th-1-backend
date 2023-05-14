package com.dnd.ground.global.batch.challenge;

import com.dnd.ground.domain.challenge.Challenge;
import com.dnd.ground.domain.challenge.ChallengeStatus;
import com.dnd.ground.domain.challenge.UserChallenge;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.global.notification.dto.NotificationForm;
import com.dnd.ground.global.notification.NotificationMessage;
import com.dnd.ground.global.util.UuidUtil;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import com.dnd.ground.global.batch.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description 챌린지-UC 간 조회를 위한 ItemWriter
 * @author  박찬호
 * @since   2023-04-14
 * @updated 1. 챌린지 종료 푸시 알람 전송 메소드 생성
 *          - 2023-04-28 박찬호
 */

@Component
@StepScope
public class ChallengeWithUCItemWriter extends JpaItemWriter<ChallengeWithUCDto> {
    private final ApplicationEventPublisher pushNotificationPublisher;
    private JobParamDateTimeConverter dateTimeConverter;

    public ChallengeWithUCItemWriter(EntityManagerFactory entityManagerFactory,
                                     ApplicationEventPublisher pushNotificationPublisher) {
        setEntityManagerFactory(entityManagerFactory);
        this.pushNotificationPublisher = pushNotificationPublisher;
    }

    public void setDateTimeConverter(JobParamDateTimeConverter dateTimeConverter) {
        this.dateTimeConverter = dateTimeConverter;
    }

    @Override
    protected void doWrite(EntityManager entityManager, List<? extends ChallengeWithUCDto> items) {
        if (!items.isEmpty()) {
            for (ChallengeWithUCDto item : items) {
                Challenge challenge = item.getChallenge();
                entityManager.merge(challenge);

                List<UserChallenge> ucs = item.getUcs();
                List<User> users = new ArrayList<>();
                for (UserChallenge uc : ucs) {
                    entityManager.merge(uc);
                }

                //푸시 알람 발송
                if (challenge.getStatus() == ChallengeStatus.PROGRESS)
                    sendChallengeStartNoti(users, challenge.getName(), challenge.getUuid());
                else if (challenge.getStatus() == ChallengeStatus.DONE)
                    sendChallengeEndNoti(users, challenge.getName(), challenge.getUuid());

            }
        }
    }

    /**
     * 챌린지 시작 푸시 알람 발송
     */
    private void sendChallengeStartNoti(List<User> users, String name, byte[] uuid) {
        if (dateTimeConverter == null) dateTimeConverter = new JobParamDateTimeConverter(LocalDate.now().toString());
        LocalDateTime reserved = dateTimeConverter.getCreated().withHour(10);
        Map<String,String> data = new HashMap<>(){{
            put("challenge_uuid", UuidUtil.bytesToHex(uuid));
        }};
        pushNotificationPublisher.publishEvent(
                new NotificationForm(users, null, List.of(name), NotificationMessage.CHALLENGE_START_SOON, data, reserved)
        );
    }

    private void sendChallengeEndNoti(List<User> users, String name, byte[] uuid) {
        if (dateTimeConverter == null) dateTimeConverter = new JobParamDateTimeConverter(LocalDateTime.now().toString());
        LocalDateTime reserved = dateTimeConverter.getCreated().withHour(8);
        Map<String,String> data = new HashMap<>(){{
            put("challenge_uuid", UuidUtil.bytesToHex(uuid));
        }};
        pushNotificationPublisher.publishEvent(
                new NotificationForm(users, null, List.of(name), NotificationMessage.CHALLENGE_RESULT, data, reserved)
        );
    }

}
