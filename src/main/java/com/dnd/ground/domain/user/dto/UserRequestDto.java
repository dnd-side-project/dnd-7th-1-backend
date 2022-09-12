package com.dnd.ground.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @description 회원 관련 Request Dto
 * @author  박세헌, 박찬호
 * @since   2022-08-18
 * @updated 1.회원가입시 사용하는 SignUp DTO 추가
 *          - 2022-09-12 박세헌
 */

@Data
public class UserRequestDto {

    /*회원가입시 사용하는 DTO*/
    @Data
    static public class SignUp {
        private String nickname;
        private List<String> friends;
        private String kakaoRefreshToken;
        //..필터 추가 예정
    }

    @Data
    static public class LookUp{

        @ApiModelProperty(name = "유저의 닉네임", example = "NickA", required = true)
        private String nickname;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
        @ApiModelProperty(name = "조회 하고 싶은 데이터의 시작 날짜", example = "2022-08-15T00:00:00",required = true)
        private LocalDateTime start;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
        @ApiModelProperty(name = "조회 하고 싶은 데이터의 끝 날짜", example = "2022-08-18T23:59:59", required = true)
        private LocalDateTime end;
    }

    @Data
    static public class Profile{

        @NotNull
        @ApiModelProperty(name = "유저의 원래 닉네임", example = "NickA", required = true)
        private String originalNick;

        @NotNull
        @ApiModelProperty(name = "수정한 닉네임", example = "NickB", required = true)
        private String editNick;

        @ApiModelProperty(name = "유저의 소개글", example = "소개글 예시")
        private String intro;

    }
}
