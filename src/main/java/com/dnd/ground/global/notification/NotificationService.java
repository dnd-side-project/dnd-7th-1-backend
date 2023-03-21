package com.dnd.ground.global.notification;

import com.dnd.ground.domain.user.User;
import com.dnd.ground.global.log.CommonLogger;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description 푸시 알람 서비스 클래스
 * @author  박찬호
 * @since   2023-03-17
 * @updated 1.FCM 앱 초기화
 *          2.메시지 구성 및 전송
 *          3.Exponential Backoff + Jitter 기반 재요청 3회 처리
 *          - 2023-03-22 박찬호
 */


@Service
@Async("notification")
@Slf4j
public class NotificationService {
    private final CommonLogger logger;
    private final NotificationRepository notificationRepository;
    private final Random random = new Random();

    public NotificationService(NotificationRepository notificationRepository, @Qualifier("notificationLogger") CommonLogger logger) {
        this.notificationRepository = notificationRepository;
        this.logger = logger;
    }

    @Value("${fcm.key.path}")
    private String FCM_PRIVATE_KEY_PATH;

    @Value("${fcm.key.scope}")
    private String FIREBASE_SCOPE;

    @Value("${fcm.project_id}")
    private String PROJECT_ID;
    private static final int MAX_RETIRES = 5;
    private static final int BASE_SLEEP_TIME = 60000;
    private static final String RETRY = "retry";
    private static final String FAIL = "fail";
    private static final String REISSUE = "reissue";

    @PostConstruct
    public void init() throws IOException {
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials
                        .fromStream(new ClassPathResource(FCM_PRIVATE_KEY_PATH).getInputStream())
                        .createScoped(List.of(FIREBASE_SCOPE))
                )
                .setProjectId(PROJECT_ID)
                .build();
        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
            logger.write("FirebaseApp init");
        }
    }

    @EventListener
    @Transactional
    public void send(NotificationForm form) {
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime reservedTime = LocalDateTime.now();
        NotificationMessage message = form.getMessage();
        List<User> users = form.getUsers();

        /*메시지 구성*/
        try {
            message.setMessageParams(form.getTitleParams(), form.getContentParams());
        } catch (NullPointerException | MissingFormatArgumentException e) {
            logger.errorWrite(String.format("푸시 알람 메시지 구성에 실패했습니다. | 메시지:%s | 제목 파라미터:%s | 내용 파라미터:%s", message, form.getTitleParams().toString(), form.getContentParams().toString()));
            return;
        }

        Notification notification = new Notification(message.getTitle(), message.getContent());
        List<Message> messages = users.stream()
                .map(user -> Message.builder()
                        .setNotification(notification)
                        .setToken(user.getProperty().getFcmToken())
                        .build())
                .collect(Collectors.toList());

        /*메시지 전송 시도*/
        BatchResponse batchResponse;
        try {
            batchResponse = FirebaseMessaging.getInstance().sendAll(messages);
        } catch (FirebaseMessagingException e) {
            String exceptionHandleResult = handleException(e);
            if (exceptionHandleResult.equals(RETRY)) {
                List<UserMsgDto> retryMessages = new ArrayList<>();
                for (int i=0; i<users.size(); i++) {
                    retryMessages.add(new UserMsgDto(users.get(i), messages.get(i)));
                }

                retry(retryMessages, message, created);
            } else if (exceptionHandleResult.equals(REISSUE)) {
                logger.errorWrite("FCM 토큰 재발급이 필요합니다.");
            } else {
                List<String> targets = users.stream()
                        .map(User::getNickname)
                        .collect(Collectors.toList());
                logger.errorWrite(String.format("푸시 알람 전송에 실패했습니다. 대상:%s | 메시지:%s, %s | 에러코드:%s", targets, message.getTitle(), message.getContent(), e.getErrorCode()));
            }
            return;
        }

        /*전송 결과 처리*/
        List<SendResponse> responses = batchResponse.getResponses();
        if (batchResponse.getFailureCount() > 0) {
            List<UserMsgDto> retryMessages = new ArrayList<>();

            for (int i = 0; i < responses.size(); i++) {
                if (!responses.get(i).isSuccessful()) {
                    User user = users.get(i);
                    Message msg = Message.builder()
                            .setNotification(notification)
                            .setToken(users.get(i).getProperty().getFcmToken())
                            .build();

                    retryMessages.add(new UserMsgDto(user, msg));
                } else {
                    notificationRepository.save(
                            new PushNotification(responses.get(i).getMessageId(), message.getTitle(), message.getContent(), created, reservedTime, NotificationStatus.SEND, users.get(i).getNickname())
                    );
                }
            }

            retry(retryMessages, message, created);
        } else {
            for (int i = 0; i < responses.size(); i++) {
                notificationRepository.save(
                        new PushNotification(responses.get(i).getMessageId(), message.getTitle(), message.getContent(), created, reservedTime, NotificationStatus.SEND, users.get(i).getNickname())
                );
            }
        }
    }

    //exponential backoff + Jitter
    private void retry(List<UserMsgDto> retryMessages, NotificationMessage message, LocalDateTime created) {
        int retries = 0;
        int waitTime;
        BatchResponse batchResponse = null;

        while (retries < MAX_RETIRES) {
            /*재전송 메시지 구성*/
            List<Message> retryMsg = retryMessages.stream()
                    .map(UserMsgDto::getMessage)
                    .collect(Collectors.toList());

            try {
                waitTime = BASE_SLEEP_TIME * ((1 << retries) + random.nextInt(4000) - 2000); //jitter range: -2~2sec
                Thread.sleep(waitTime);
                batchResponse = FirebaseMessaging.getInstance().sendAll(retryMsg);
            } catch (FirebaseMessagingException e) {
                String result = handleException(e);
                if (result.equals(RETRY)) {
                    retries++;
                    continue;
                } else {
                    for (UserMsgDto retryMessage : retryMessages) {
                        User user = retryMessage.getUser();
                        String messageId = "ERROR_" + user.getNickname() + LocalDateTime.now() + random.nextInt(100);
                        notificationRepository.save(
                                new PushNotification(messageId, message.getTitle(), message.getContent(), created, LocalDateTime.now(), NotificationStatus.FAIL, user.getNickname())
                        );
                        logger.errorWrite(String.format("푸시 알람 재전송에 실패했습니다. 대상:%s | 메시지:%s, %s | 에러코드:%s", user.getNickname(), message.getTitle(), message.getContent(), e.getErrorCode()));
                    }
                    return;
                }
            } catch (InterruptedException e) {
                retries++;
                continue;
            }

            /*재전송 결과에 따른 처리*/
            List<SendResponse> responses = batchResponse.getResponses();

            if (batchResponse.getFailureCount() > 0) {
                /*전송 실패한 메시지가 존재하는 경우 재전송 시도*/
                List<UserMsgDto> successMessages = new ArrayList<>();

                for (int i = 0; i < responses.size(); i++) {
                    UserMsgDto userMsgDto = retryMessages.get(i);
                    SendResponse sendResponse = responses.get(i);

                    if (sendResponse.isSuccessful()) {
                        notificationRepository.save(
                                new PushNotification(sendResponse.getMessageId(), message.getTitle(), message.getContent(), created, LocalDateTime.now(), NotificationStatus.SEND, userMsgDto.getUser().getNickname())
                        );
                        successMessages.add(userMsgDto);
                    }
                }
                retryMessages.removeAll(successMessages);
                retries++;
            } else {
                /*전송 성공한 경우 저장*/
                for (int i = 0; i < responses.size(); i++) {
                    User user = retryMessages.get(i).getUser();
                    SendResponse response = responses.get(i);
                    notificationRepository.save(
                            new PushNotification(response.getMessageId(), message.getTitle(), message.getContent(), created, LocalDateTime.now(), NotificationStatus.SEND, user.getNickname())
                    );
                }
                break;
            }
        }

        for (int i=0; i<retryMessages.size(); i++) {
            User user = retryMessages.get(i).getUser();
            String messageId = "ERROR_" + user.getNickname() + LocalDateTime.now() + random.nextInt(100);
            notificationRepository.save(
                    new PushNotification(messageId, message.getTitle(), message.getContent(), created, LocalDateTime.now(), NotificationStatus.FAIL, user.getNickname())
            );
            String errorCode = batchResponse == null ? "에러 코드를 알 수 없습니다." : batchResponse.getResponses().get(i).getException().getErrorCode();
            logger.errorWrite(String.format("푸시 알람 재전송에 실패했습니다. 대상:%s | 메시지:%s, %s | 에러코드:%s", user.getNickname(), message.getTitle(), message.getContent(), errorCode));
        }
    }

    private String handleException(FirebaseMessagingException e) {
        switch (e.getErrorCode()) {
            case "UNSPECIFIED_ERROR":
            case "QUOTA_EXCEEDED":
            case "UNAVAILABLE":
            case "INTERNAL":
                return RETRY;
            case "UNREGISTERED":
                return REISSUE;
            case "INVALID_ARGUMENT":
            case "SENDER_ID_MISMATCH":
            case "THIRD_PARTY_AUTH_ERROR":
            default:
                return FAIL;
        }
    }
}