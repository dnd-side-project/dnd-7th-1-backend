package com.dnd.ground.global.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.MissingFormatArgumentException;

/**
 * @description 푸시 알람 메시지 목록
 * @author  박찬호
 * @since   2023-03-20
 * @updated 1.메시지 속 파라미터를 파싱하는 방식 변경
 *          -2023-04-10 박찬호
 */


@AllArgsConstructor
@Slf4j
@Getter
public enum NotificationMessage {

    /**
     * 공통
     */
    COMMON_WEEK_START("주차 시작 알림", "기록과 챌린지를 새롭게 시작해보세요."),
    COMMON_WEEK_END("주차 종료 알림", "이번 주차 기록/챌린지가 자정에 종료돼요."),

    /**
     * 친구
     */
    FRIEND_RECEIVED_REQUEST("%s님의 친구 요청 ", "회원님의 친구 수락을 기다리고 있어요."),
    FRIEND_ACCEPT("%s님과 친구가 되었어요.", "이제 메인 화면에서 친구를 확인할 수 있어요."),

    /**
     * 챌린지
     */
    CHALLENGE_RECEIVED_REQUEST("%s님의 챌린지 초대", "%s에 초대했어요."),
    CHALLENGE_ACCEPTED("%s님의 챌린지 수락", "%s님이 챌린지를 수락했어요."),
    CHALLENGE_START_SOON("챌린지 진행 안내", "%s개의 챌린지가 이번 주차에 시작됩니다."),
    CHALLENGE_CANCELED("챌린지 취소", "수락한 인원이 없어서 챌린지가 취소되었어요."),
    CHALLENGE_RESULT("지난 주 챌린지 결과 안내", "%s개의 챌린지가 종료되었어요.");


    private String title;
    private String content;

    public String getTitle(List<String> titleParams) {
        return parse(this.title, titleParams);
    }

    public String getContent(List<String> contentParams) {
        return parse(this.content, contentParams);
    }

    private String parse(String format, List<String> params) {
        if (params == null) return format;
        else if (StringUtils.countOccurrencesOf(format, "%s") != params.size()) throw new MissingFormatArgumentException("파라미터 개수가 올바르지 않습니다.");

        for (String param : params) {
            format = format.replaceFirst("%s", param);
        }
        return format;
    }
}
