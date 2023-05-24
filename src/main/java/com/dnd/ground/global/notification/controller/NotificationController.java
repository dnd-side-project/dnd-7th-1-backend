package com.dnd.ground.global.notification.controller;

import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.repository.UserRepository;
import com.dnd.ground.global.exception.ExceptionCodeSet;
import com.dnd.ground.global.notification.dto.NotificationDeleteRequestDto;
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

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * @description 푸시 알람 관련 컨트롤러
 * @author  박찬호
 * @since   2023-05-13
 * @updated  1.알람 비우기 API 구현
 *          - 2023-05-24 박찬호
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

    @PostMapping("/delete")
    @Operation(summary = "푸시 알람 삭제(비우기)", description = "메시지 ID 목록을 보내주면 이후 알림함에서 조회되지 않습니다.")
    public ResponseEntity<ExceptionCodeSet> deleteNotification(@Valid @RequestBody NotificationDeleteRequestDto messageIds) {
        return ResponseEntity.ok().body(notificationService.deleteNotification(messageIds.getNotifications()));
    }
}
