package com.dnd.ground.domain.challenge.dto;

import com.dnd.ground.domain.challenge.ChallengeType;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * @description 챌린지 생성과 관련한 Request DTO
 * @author  박찬호
 * @since   2022-08-03
 * @updated 1. 시간 필드 타입 LocalDate -> LocalDateTime으로 변경
 *          - 2023.02.27
 */

@Data
@AllArgsConstructor
public class ChallengeCreateRequestDto {

    @NotBlank(message = "주최자의 닉네임이 필요합니다.")
    @ApiModelProperty(value="닉네임", example="NickA", required = true)
    private String nickname;
    
    @NotBlank(message = "챌린지 이름이 필요합니다.")
    @ApiModelProperty(value="챌린지 이름", example="챌린지1", required = true)
    private String name;

    @ApiModelProperty(value="신청 메시지", example="챌린지 신청 메시지")
    private String message;

    @JsonDeserialize(using= LocalDateTimeDeserializer.class)
    @JsonSerialize(using= LocalDateTimeSerializer.class)
    @ApiModelProperty(value = "챌린지 시작 날짜", example = "2022-08-04T00:00:00")
    private LocalDateTime started;

    @ApiModelProperty(value="챌린지 종류(영역: WIDEN || 칸: ACCUMULATE)", example="ACCUMULATE", required = true)
    private ChallengeType type;

    @Size(min=1, message = "함께하는 친구가 1명 이상이어야 합니다.")
    @ApiModelProperty(value="함께하는 친구 닉네임 리스트", example="[NickB, NickC]", required = true)
    private Set<String> friends;

}
