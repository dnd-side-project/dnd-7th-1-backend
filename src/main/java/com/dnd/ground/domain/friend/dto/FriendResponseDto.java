package com.dnd.ground.domain.friend.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @description 친구와 관련한 정보 조회용 Response DTO
 * @author  박찬호
 * @since   2022-08-02
 * @updated 1. Swagger를 위한 API 명세 추가
 *          - 2022.08.03 박찬호
 */

@Data @Builder
public class FriendResponseDto {

    @ApiModelProperty(value="친구 정보", example="[nickname:nickA, ..]")
    private List<Info> infos;

    @ApiModelProperty(value="친구 수", example="3")
    private Integer size;

    
    //친구와 관련한 정보 모음
    @Data @NoArgsConstructor
    static public class Info {

        @Builder(builderMethodName = "of")
        public Info(String nickname) {
            this.nickname = nickname;
        }

        private String nickname;
    }


}
