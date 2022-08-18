package com.dnd.ground.domain.user.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @description 회원 관련 Request Dto
 * @author  박세헌, 박찬호
 * @since   2022-08-18
 * @updated nickname, start, end 가진 requestDto 생성
 *          - 2022-08-18 박세헌
 */

@Data
public class UserRequestDto {

    @Data
    static public class LookUp{
        private String nickname;
        private LocalDateTime start;
        private LocalDateTime end;
    }
}
