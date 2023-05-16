package com.dnd.ground.domain.friend.dto;

import com.dnd.ground.domain.friend.FriendStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @description 친구와 관련한 정보 조회용 Request DTO
 * @author  박찬호
 * @since   2022.10.10
 * @updated 1. @Data 어노테이션 제거
 *          2. 친구 목록 조회 데이터
 *          3. 친구 목록 조회 DTO 생성
 *          - 2023.05.16 박찬호
 */

public class FriendRequestDto {

    @Getter
    @AllArgsConstructor
    public static class FriendList {
        @NotNull
        private String nickname;

        @Min(1)
        private Long offset;

        @NotNull
        @Min(0)
        private Integer size;
    }

    /*친구 요청을 위한 Request DTO*/
    @Getter
    public static class Request {
        @ApiModelProperty(value="본인 닉네임(요청 하는 사람)", example="NickA")
        private String userNickname;

        @ApiModelProperty(value="친구 닉네임(요청 받는 사람)", example="NickB")
        private String friendNickname;
    }

    /*친구 요청 응답을 위한 Request DTO*/
    @Getter
    public static class Response {
        @ApiModelProperty(value="본인 닉네임(요청 받는 사람)", example="NickA")
        private String userNickname;

        @ApiModelProperty(value="친구 닉네임(요청 하는 사람)", example="NickB")
        private String friendNickname;

        @ApiModelProperty(value="요청에 대한 응답(수락: Accept, 거절: Reject)", example="Accept")
        private FriendStatus status;
    }
}
