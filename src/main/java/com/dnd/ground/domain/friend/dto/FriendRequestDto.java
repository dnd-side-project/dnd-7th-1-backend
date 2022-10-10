package com.dnd.ground.domain.friend.dto;

import com.dnd.ground.domain.friend.FriendStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description 친구와 관련한 정보 조회용 Request DTO
 * @author  박찬호
 * @since   2022.10.10
 * @updated 1. Profile 클래스 이동(UserResponseDto -> FriendResponseDto) 및 이름 변경(Profile -> FriendProfile)
 *          - 2022.08.26 박찬호
 */

public class FriendRequestDto {

    /*친구 요청을 위한 Request DTO*/
    @Data
    static public class Request {
        @ApiModelProperty(value="본인 닉네임(요청 하는 사람)", example="NickA")
        private String userNickname;

        @ApiModelProperty(value="친구 닉네임(요청 받는 사람)", example="NickB")
        private String friendNickname;
    }

    /*친구 요청 응답을 위한 Request DTO*/
    @Data
    static public class Response {
        @ApiModelProperty(value="본인 닉네임(요청 받는 사람)", example="NickA")
        private String userNickname;

        @ApiModelProperty(value="친구 닉네임(요청 하는 사람)", example="NickB")
        private String friendNickname;

        @ApiModelProperty(value="요청에 대한 응답(수락: Accept, 거절: Reject)", example="Accept")
        private FriendStatus status;
    }
}
