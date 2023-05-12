package com.dnd.ground.global.notification.service;

import com.dnd.ground.domain.user.User;
import com.dnd.ground.global.log.CommonLogger;
import com.dnd.ground.global.notification.*;
import com.dnd.ground.global.notification.dto.NotificationForm;
import com.dnd.ground.global.notification.dto.UserMsgDto;
import com.dnd.ground.global.notification.repository.FcmTokenRepository;
import com.dnd.ground.global.notification.repository.NotificationRepository;
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
import org.springframework.transaction.annotation.Propagation;
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
 * @updated 1.메시지 구성 방식 및 재발급 로직 변경
 *          - 2023-05-11 박찬호
 */

@Service
@Async("notification")
@Slf4j
public class FcmService {
    private final CommonLogger logger;
    private final NotificationRepository notificationRepository;
    private final FcmTokenRepository fcmTokenRepository;
    private final Random random = new Random();
    private static final String DATA_PARAM_ACTION = "action";
    private static final String DATA_PARAM_MESSAGE_ID = "message_id";

    private static final String EXCEPTION_RESULT_RETRY = "retry";
    private static final String EXCEPTION_RESULT_FAIL = "fail";
    private static final String EXCEPTION_RESULT_REISSUE = "reissue";
    private static final int MAX_RETIRES = 5;
    private static final int BASE_SLEEP_TIME = 60000;

    public FcmService(NotificationRepository notificationRepository,
                      FcmTokenRepository fcmTokenRepository,
                      @Qualifier("notificationLogger") CommonLogger logger) {
        this.notificationRepository = notificationRepository;
        this.fcmTokenRepository = fcmTokenRepository;
        this.logger = logger;
    }

    @Value("${fcm.key.path}")
    private String FCM_PRIVATE_KEY_PATH;

    @Value("${fcm.key.scope}")
    private String FIREBASE_SCOPE;

    @Value("${fcm.project_id}")
    private String PROJECT_ID;

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
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void messageInit(NotificationForm form) {
        List<User> users = form.getUsers();
        if (users.isEmpty()) return;

        LocalDateTime created = form.getCreated() != null ? form.getCreated() : LocalDateTime.now();
        LocalDateTime reserved = form.getReserved() != null ? form.getReserved() : LocalDateTime.now();
        NotificationMessage message = form.getMessage();
        PushNotificationType type = NotificationMessage.getType(message);

        Map<String, String> data = form.getData() != null ? form.getData() : new HashMap<>();
        data.put(DATA_PARAM_ACTION, message.name());

        /*메시지 구성*/
        String title;
        String content;

        try {
            title = message.getTitle(form.getTitleParams());
            content = message.getContent(form.getContentParams());
        } catch (NullPointerException | MissingFormatArgumentException e) {
            logger.errorWrite(String.format("푸시 알람 메시지 구성에 실패했습니다. | 메시지:%s | 제목 파라미터:%s | 내용 파라미터:%s", message, form.getTitleParams().toString(), form.getContentParams().toString()));
            return;
        }

        //PushNotification 객체 구성
        List<PushNotification> notifications = new ArrayList<>();
        List<PushNotificationParam> params = new ArrayList<>();

        for (PushNotificationParamList param : PushNotificationParamList.values()) {
            String paramKey = param.name().toLowerCase();
            if (data.containsKey(paramKey)) {
                params.add(new PushNotificationParam(param, data.get(paramKey)));
            }
        }

        for (User user : users) {
            PushNotification notification = new PushNotification(getMessageId(), title, content, created, reserved, NotificationStatus.WAIT, user.getNickname(), type);
            notification.setParams(params);
            notifications.add(notification);
        }

        if (reserved.compareTo(LocalDateTime.now()) <= 0) {
            send(notifications);
        } else {
            //메시지 예약
            notifications.forEach(n -> n.updateStatus(NotificationStatus.RESERVED));
            notificationRepository.saveAll(notifications);
        }
    }

    private void send(List<PushNotification> notifications) {
        List<Message> messages = new ArrayList<>();

        for (PushNotification pn : notifications) {
            List<String> tokens = fcmTokenRepository.findAllTokens(pn.getNickname());
            for (String token : tokens) {
                messages.add(
                        Message.builder()
                                .setNotification(new Notification(pn.getTitle(), pn.getContent()))
                                .setToken(token)
                                .putAllData(pn.getParamMap())
                                .putData(DATA_PARAM_MESSAGE_ID, pn.getMessageId())
                                .build()
                );
            }
        }

        if (messages.isEmpty()) {
            notifications.forEach(n -> {
                logger.errorWrite(String.format("메시지가 생성되지 않아 푸시 알람 전송에 실패했습니다: 닉네임:%s", n.getNickname()));
                n.updateStatus(NotificationStatus.FAIL);
            });
            return;
        }

        /*메시지 전송 시도*/
        BatchResponse batchResponse;
        try {
            batchResponse = FirebaseMessaging.getInstance().sendAll(messages);
        } catch (FirebaseMessagingException e) {
            String exceptionHandleResult = handleException(e);

            if (exceptionHandleResult.equals(EXCEPTION_RESULT_RETRY)) {
                //재전송 시도
                List<UserMsgDto> retryMessages = new ArrayList<>();

                for (int i = 0; i < notifications.size(); i++) {
                    retryMessages.add(new UserMsgDto(notifications.get(i).getNickname(), messages.get(i), notifications.get(i)));
                }

                retry(retryMessages);

            } else if (exceptionHandleResult.equals(EXCEPTION_RESULT_REISSUE)) {
                //토큰 재발급 요청
                List<String> nicknames = notifications.stream()
                        .map(PushNotification::getNickname)
                        .collect(Collectors.toList());

                for (String nickname : nicknames) {
                    List<String> tokens = fcmTokenRepository.findAllTokens(nickname);
                    for (String token : tokens) {
                        requestReissueFCMToken(nickname, token);
                    }
                }
            } else {
                //발송 실패
                logger.errorWrite(String.format("푸시 알람 전송에 실패했습니다. 시간:%s 에러코드:%s", LocalDateTime.now(), e.getErrorCode()));
                notifications.forEach(n -> {
                            n.updateStatus(NotificationStatus.FAIL);
                            notificationRepository.save(n);
                        }
                );
            }
            return;
        }

        /*전송 결과 처리*/
        List<SendResponse> responses = batchResponse.getResponses();
        if (batchResponse.getFailureCount() > 0) {
            List<UserMsgDto> retryMessages = new ArrayList<>();

            for (int i = 0; i < responses.size(); i++) {
                PushNotification pushNotification = notifications.get(i);
                //전송 1번에 대한 결과이므로 인덱스 접근 가능
                if (responses.get(i).isSuccessful()) {
                    pushNotification.updateStatus(NotificationStatus.SEND);
                    notificationRepository.save(pushNotification);
                } else {
                    retryMessages.add(new UserMsgDto(pushNotification.getNickname(), messages.get(i), pushNotification));
                }
            }

            retry(retryMessages);
        } else {
            notifications.forEach(n -> {
                n.updateStatus(NotificationStatus.SEND);
                notificationRepository.save(n);
            });
        }
    }

    //exponential backoff + Jitter
    private void retry(List<UserMsgDto> retryMessages) {
        int retries = 0;
        int waitTime;
        BatchResponse batchResponse;

        while (retries < MAX_RETIRES) {
            /*재전송 메시지 구성*/
            List<Message> retryMsg = retryMessages.stream()
                    .map(UserMsgDto::getMessage)
                    .collect(Collectors.toList());

            try {
                waitTime = BASE_SLEEP_TIME + ((1 << retries) * Math.max(1, random.nextInt(4000) - 2000)); //jitter range: -2~2sec
                Thread.sleep(waitTime);
                batchResponse = FirebaseMessaging.getInstance().sendAll(retryMsg);
            } catch (FirebaseMessagingException e) {
                String result = handleException(e);

                if (result.equals(EXCEPTION_RESULT_RETRY)) {
                    retries++;
                    continue;
                } else if (result.equals(EXCEPTION_RESULT_REISSUE)) {
                    //토큰 재발급 요청
                    List<String> nicknames = retryMessages.stream()
                            .map(UserMsgDto::getNickname)
                            .collect(Collectors.toList());

                    for (String nickname : nicknames) {
                        List<String> tokens = fcmTokenRepository.findAllTokens(nickname);
                        for (String token : tokens) {
                            requestReissueFCMToken(nickname, token);
                        }
                    }
                } else {
                    //전송 실패
                    logger.errorWrite(String.format("푸시 알람 재전송에 실패했습니다. 시간:%s | 에러코드:%s", LocalDateTime.now(), e.getErrorCode()));
                    retryMessages.forEach(rm -> {
                        PushNotification notification = rm.getNotification();
                        notification.updateStatus(NotificationStatus.FAIL);
                        notificationRepository.save(notification);
                    });
                }
                return;
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
                        PushNotification notification = userMsgDto.getNotification();
                        notification.updateStatus(NotificationStatus.SEND);
                        notificationRepository.save(notification);

                        successMessages.add(userMsgDto);
                    }
                }
                retryMessages.removeAll(successMessages);
                retries++;
            } else {
                /*전송 성공한 경우 저장*/
                log.info("FCM 알람: {}번 시도 후 성공", retries);

                retryMessages.forEach(rm -> {
                    PushNotification notification = rm.getNotification();
                    notification.updateStatus(NotificationStatus.SEND);
                    notificationRepository.save(notification);
                });
                return;
            }
        }

        if (retryMessages.isEmpty()) {
            log.info("FCM 알람: {}번 시도 후 성공", retries);
        } else {
            for (UserMsgDto message : retryMessages) {
                PushNotification notification = message.getNotification();
                notification.updateStatus(NotificationStatus.FAIL);
                notificationRepository.save(notification);
            }
        }
    }

    /*FCM 토큰 재발급 요청 (Silent Message)*/
    public static void requestReissueFCMToken(String nickname, String token) {
        if (token == null) {
            log.warn("토큰이 존재하지 않습니다: {}", nickname);
            return;
        }

        Message msg = Message.builder()
                .setToken(token)
                .putData(DATA_PARAM_ACTION, NotificationMessage.COMMON_REISSUE_FCM_TOKEN.name())
                .setApnsConfig(
                        ApnsConfig.builder()
                                .putHeader("apns-priority", "5")
                                .putHeader("apns-push-type", "background")
                                .setAps(
                                        Aps.builder()
                                                .setCategory("REISSUE")
                                                .setContentAvailable(true)
                                                .build()
                                )
                                .build()
                )
                .build();

        try {
            FirebaseMessaging.getInstance().send(msg);
            log.info("토큰 재발급 요청에 성공했습니다: {}", nickname);
        } catch (FirebaseMessagingException e) {
            log.warn("토큰 재발급 요청에 실패했습니다: {}", nickname);
        }
    }

    private String getMessageId() {
        return System.currentTimeMillis() + String.valueOf(random.nextInt(1000) + 1);
    }

    private String handleException(FirebaseMessagingException e) {
        switch (e.getErrorCode()) {
            case "UNSPECIFIED_ERROR":
            case "QUOTA_EXCEEDED":
            case "UNAVAILABLE":
            case "INTERNAL":
                return EXCEPTION_RESULT_RETRY;
            case "UNREGISTERED":
                return EXCEPTION_RESULT_REISSUE;
            case "INVALID_ARGUMENT":
            case "SENDER_ID_MISMATCH":
            case "THIRD_PARTY_AUTH_ERROR":
            default:
                return EXCEPTION_RESULT_FAIL;
        }
    }
}