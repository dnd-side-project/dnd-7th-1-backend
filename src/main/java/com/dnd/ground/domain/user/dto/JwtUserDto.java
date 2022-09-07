package com.dnd.ground.domain.user.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description 카카오 정보를 받기 위한 dto
 * @author  박세헌
 * @since   2022-08-24
 * @updated dto 생성
 *          - 2022.08.24 박세헌
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JwtUserDto {
    private Long id;  // 카카오 id
    private String username;
    private String nickname;
    private String mail;
}