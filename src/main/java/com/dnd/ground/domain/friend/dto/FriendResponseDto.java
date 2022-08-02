package com.dnd.ground.domain.friend.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @description 친구와 관련한 정보 조회용 Response DTO
 * @author  박찬호
 * @since   2022-08-02
 * @updated 1. 내부 Info 클래스를 통해 친구와 관련한 정보 반환
 *          - 2022.08.02 박찬호
 */

@Data @Builder
public class FriendResponseDto {

    private List<Info> infos;
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
