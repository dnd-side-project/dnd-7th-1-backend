package com.dnd.ground.global.notification;

import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.UserProperty;
import com.dnd.ground.domain.user.repository.UserPropertyRepository;
import com.dnd.ground.domain.user.repository.UserRepository;
import com.dnd.ground.global.exception.ExceptionCodeSet;
import com.dnd.ground.global.exception.UserException;
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
import org.springframework.data.redis.core.RedisTemplate;
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
 * @updated 1.PushNotification 객체를 활용해서 각 메소드간 데이터 전달  -> 엔티티가 DTO 역할을 같이 해서 좋지 않은 코드일 수 있다.
 *                                                              비슷한 파라미터가 많고, 똑같은 DTO를 생성하는 것도 복잡도만 올라간다고 판단.
 *                                                              예약 알람까지 개발 후 개선 예정
 *          2.메시지 전처리, 전송, 재전송 메소드 개선
 *          3.FCM 토큰 재발급 요청 메소드 추가
 *          - 2023-05-05 박찬호
 */

@Service
@Async("notification")
@Slf4j
public class NotificationService {
    private final CommonLogger logger;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final UserPropertyRepository userPropertyRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final Random random = new Random();
    private static final String DATA_PARAM_ACTION = "action";
    private static final String DATA_PARAM_MESSAGE_ID = "message_id";

    private static final String EXCEPTION_RESULT_RETRY = "retry";
    private static final String EXCEPTION_RESULT_FAIL = "fail";
    private static final String EXCEPTION_RESULT_REISSUE = "reissue";
    private static final int MAX_RETIRES = 5;
    private static final int BASE_SLEEP_TIME = 60000;

    public NotificationService(NotificationRepository notificationRepository,
                               UserRepository userRepository,
                               UserPropertyRepository userPropertyRepository,
                               RedisTemplate<String, String> redisTemplate,
                               @Qualifier("notificationLogger") CommonLogger logger) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.userPropertyRepository = userPropertyRepository;
        this.redisTemplate = redisTemplate;
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
        LocalDateTime created = form.getCreated() != null ? form.getCreated() : LocalDateTime.now();
        LocalDateTime reserved = form.getReserved() != null ? form.getReserved() : LocalDateTime.now();

        NotificationMessage message = form.getMessage();
        if (message == null) {
            logger.errorWrite("메시지가 존재하지 않습니다.");
            return;
        }

        Map<String, String> data = form.getData() != null ? form.getData() : new HashMap<>();
        data.put(DATA_PARAM_ACTION, message.name());

        //트랜잭션 분리에 따른 UserProperty 프록시 확인 및 필터 확인
        List<User> exceptUsers = new ArrayList<>();
        for (User user : users) {
            if (user.getProperty().getFcmToken() == null) {
                Optional<UserProperty> userPropertyOpt = userPropertyRepository.findByNickname(user.getNickname());
                if (userPropertyOpt.isPresent()) {
                    user.setUserProperty(userPropertyOpt.get());
                } else {
                    logger.errorWrite(String.format("회원 정보 조회에 실패했습니다. | 닉네임:%s", user.getNickname()));
                    return;
                }
            }

            if (!user.getProperty().checkNotiFilter(message)) exceptUsers.add(user);
        }

        users.removeAll(exceptUsers);
        if (users.isEmpty()) return;

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
            PushNotification notification = new PushNotification(getMessageId(), title, content, created, reserved, NotificationStatus.WAIT, user.getNickname());
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
            String fcmToken = (String) redisTemplate.opsForHash().get("fcm:" + pn.getNickname(), "fcmToken");
            if (fcmToken == null) {
                fcmToken = userPropertyRepository.findByNickname(pn.getNickname())
                        .orElseThrow(() -> new UserException(ExceptionCodeSet.USER_NOT_FOUND))
                        .getFcmToken();
            }

            messages.add(
                    Message.builder()
                            .setNotification(new Notification(pn.getTitle(), pn.getContent()))
                            .setToken(fcmToken)
                            .putAllData(pn.getParamMap())
                            .putData(DATA_PARAM_MESSAGE_ID, pn.getMessageId())
                            .build()
            );
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

                userRepository.findAllByNickname(nicknames)
                        .forEach(NotificationService::requestReissueFCMToken);
            } else {
                //발송 실패
                logger.errorWrite(String.format("푸시 알람 전송에 실패했습니다. 시간:%s 에러코드:%s", LocalDateTime.now().toString(), e.getErrorCode()));
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

                    userRepository.findAllByNickname(nicknames)
                            .forEach(NotificationService::requestReissueFCMToken);
                } else {
                    //전송 실패
                    logger.errorWrite(String.format("푸시 알람 재전송에 실패했습니다. 시간:%s | 에러코드:%s", LocalDateTime.now().toString(), e.getErrorCode()));
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
    public static void requestReissueFCMToken(User user) {
        Message msg = Message.builder()
                .setToken(user.getProperty().getFcmToken())
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
        } catch (FirebaseMessagingException e) {
            log.warn("토큰 재발급 요청에 실패했습니다. user:{}", user.getNickname());
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