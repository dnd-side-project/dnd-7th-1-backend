package com.dnd.ground.domain.user.service;

import com.dnd.ground.domain.exerciseRecord.dto.RecordRequestDto;
import com.dnd.ground.domain.exerciseRecord.dto.RecordResponseDto;
import com.dnd.ground.domain.friend.dto.FriendResponseDto;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * @description 회원 서비스 인터페이스
 * @author  박세헌, 박찬호
 * @since   2022-08-01
 * @updated 운동 기록 날짜 조회 함수 추가
 *          - 2022-09-24 박세헌
 */

public interface UserService {
    HomeResponseDto showHome(String nickname);
    UserResponseDto.Profile getUserInfo(String nickname);

    FriendResponseDto.FriendProfile getUserProfile(String userNickname, String friendNickname);
    ActivityRecordResponseDto getActivityRecord(UserRequestDto.LookUp requestDto);
    RecordResponseDto.EInfo getExerciseInfo(Long exerciseId);
    UserResponseDto.DetailMap getDetailMap(Long recordId);

    Boolean changeFilterMine(String nickname);
    Boolean changeFilterFriend(String nickname);
    Boolean changeFilterRecord(String nickname);

    ResponseEntity<Boolean> editRecordMessage(RecordRequestDto.Message requestDto);
    ResponseEntity<Boolean> editUserProfile(UserRequestDto.Profile requestDto);

    UserResponseDto.dayEventList getDayEventList(UserRequestDto.dayEventList requestDto);
}
