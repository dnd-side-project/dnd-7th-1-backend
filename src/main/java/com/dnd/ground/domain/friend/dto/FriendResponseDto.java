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
 * @updated 1. API 명세 수정
 *          - 2022.08.18 박찬호
 */

@Data @Builder
public class FriendResponseDto {

    @ApiModelProperty(value="친구 정보 리스트", example="[NickB, NickC]")
    private List<FInfo> infos;

    @ApiModelProperty(value="친구 수", example="3")
    private Integer size;

    
    //친구와 관련한 정보 모음
    @Data @NoArgsConstructor
    static public class FInfo {

        @Builder(builderMethodName = "of")
        public FInfo(String nickname) {
            this.nickname = nickname;
        }
        
        @ApiModelProperty(value="닉네임", example="NickA")
        private String nickname;
    }


}
