package com.dnd.ground.global.notification.controller;

import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.repository.UserRepository;
import com.dnd.ground.global.exception.ExceptionCodeSet;
import com.dnd.ground.global.notification.dto.NotificationForm;
import com.dnd.ground.global.notification.NotificationMessage;
import com.dnd.ground.global.notification.dto.NotificationResponseDto;
import com.dnd.ground.global.notification.service.NotificationService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @description 푸시 알람 관련 컨트롤러
 * @author  박찬호
 * @since   2023-05-13
 * @updated  1.푸시 알람 조회 API 구현
 *           2.푸시 알람 읽기 API 구현
 *          - 2023-05-13 박찬호
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/noti")
@Api(tags = "푸시알람")
public class NotificationController {

    private final ApplicationEventPublisher eventPublisher;
    private final UserRepository userRepository;
    private final NotificationService notificationService;


    @GetMapping("")
    @Operation(summary = "푸시 알람 목록 조회", description = "실패한 메시지를 제외하고 최근 20개의 메시지를 전달합니다.")
    public ResponseEntity<List<NotificationResponseDto>> getNotifications(@RequestParam("nickname") String nickname) {
        return ResponseEntity.ok().body(notificationService.getNotifications(nickname));
    }


    @PostMapping("")
    @Operation(summary = "푸시 알람 읽기", description = "MessageID로 요청이 들어오면, 해당 알람이 읽혔는지 여부가 TRUE로 처리됩니다.")
    public ResponseEntity<ExceptionCodeSet> readNotification(@RequestParam("messageId") String messageId) {
        return ResponseEntity.ok().body(notificationService.readNotification(messageId));
    }

    @GetMapping("/dummy/noti")
    @Operation(summary = "이거 지금 동작 안해요!!!", description = "토큰 리스트 형태로 보내주시면 됩니다.\n개발 완성이 아니기 때문에, 전송 실패에 대한 처리나 예외 처리, 디버깅에 한계가 있어요!")
    public void send(@RequestParam("tokens")ArrayList<String> tokens) {
        User nickA = userRepository.findByNicknameWithProperty("user2").get();
        User nickB = userRepository.findByNicknameWithProperty("user3").get();
        List<User> users = new ArrayList<>();
        users.add(nickA);
//        users.add(nickB);
        eventPublisher.publishEvent(new NotificationForm(users, List.of("타이틀1"), List.of("타이틀2"), NotificationMessage.CHALLENGE_RECEIVED_REQUEST));
//        eventPublisher.publishEvent(new NotificationForm(tokens));
    }

}
