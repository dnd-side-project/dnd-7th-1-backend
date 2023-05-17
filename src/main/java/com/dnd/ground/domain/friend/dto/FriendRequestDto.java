package com.dnd.ground.domain.friend.dto;

import com.dnd.ground.domain.friend.FriendStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @description 친구와 관련한 정보 조회용 Request DTO
 * @author  박찬호
 * @since   2022.10.10
 * @updated 1.친구 삭제 벌크 API를 위한 DTO 생성
 *          - 2023.05.17 박찬호
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

    /*친구 벌크 삭제를 위한 Request DTO*/
    @Getter
    public static class Bulk {
        @ApiModelProperty(value="본인 닉네임", example="nick1")
        private String nickname;

        @ApiModelProperty(value="친구 닉네임 배열", example="[\"nick2\", \"nick3\"]")
        private List<String> friends;
    }
}
