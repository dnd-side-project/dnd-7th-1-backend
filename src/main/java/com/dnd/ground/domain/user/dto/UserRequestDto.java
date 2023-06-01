package com.dnd.ground.domain.user.dto;

import com.dnd.ground.domain.matrix.dto.Location;
import com.dnd.ground.global.notification.NotificationMessage;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @description 회원 관련 Request Dto
 * @author  박세헌, 박찬호
 * @since   2022-08-18
 * @updated 1.푸시 알람 필터 변경을 위한 DTO 추가
 *          2.미사용 SignUp 클래스 삭제
 *          3.@Data 어노테이션 삭제
 *          - 2023-04-13 박찬호
 */

public class UserRequestDto {

    @Getter
    @Setter
    @ToString
    static public class Home {
        public Home(String nickname, Double spanDelta, Double latitude, Double longitude) {
            this.nickname = nickname;
            this.center = new Location(latitude, longitude);
            this.spanDelta = spanDelta;
        }

        @NotBlank
        @ApiModelProperty(name = "유저의 닉네임", example = "NickA", required = true)
        private String nickname;

        @NotBlank
        @ApiModelProperty(name = "현재 위치", required = true)
        private Location center;

        @NotBlank
        @ApiModelProperty(name = "영역을 조회할 범위(km)", example="0.03", required = true)
        private Double spanDelta;
    }


    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    static public class LookUp{
        @ApiModelProperty(name = "유저의 닉네임", example = "NickA", required = true)
        private String nickname;

        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @ApiModelProperty(name = "조회 하고 싶은 데이터의 시작 날짜", example = "2022-08-15T00:00:00", required = true)
        private LocalDateTime started;

        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @ApiModelProperty(name = "조회 하고 싶은 데이터의 끝 날짜", example = "2022-08-18T23:59:59", required = true)
        private LocalDateTime ended;
    }

    @Getter
    @AllArgsConstructor
    static public class Profile {

        @NotNull
        @ApiModelProperty(name = "유저의 원래 닉네임", example = "NickA", required = true)
        private String originNickname;

        @NotNull
        @ApiModelProperty(name = "수정한 닉네임", example = "NickB", required = true)
        private String editNickname;

        @ApiModelProperty(name = "유저의 소개글", example = "소개글 예시")
        private String intro;

        @ApiModelProperty(name = "네모두 기본 사진 변경 여부", example = "false")
        private Boolean isBasic;
    }

    /* 운동 기록 날짜 조회시 사용하는 dto */
    @Getter
    @AllArgsConstructor
    static public class DayEventList {
        @ApiModelProperty(name = "닉네임", example = "NickA", required = true)
        private String nickname;

        @ApiModelProperty(name = "년-월-날짜", example = "2022-09-01", required = true)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate yearMonth;
    }

    @AllArgsConstructor
    @Getter
    static public class NotificationFilter {
        private String nickname;
        private NotificationMessage notification;
    }
}
