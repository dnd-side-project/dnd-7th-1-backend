package com.dnd.ground.domain.user.service;

import com.dnd.ground.domain.exerciseRecord.dto.RecordRequestDto;
import com.dnd.ground.domain.exerciseRecord.dto.RecordResponseDto;
import com.dnd.ground.domain.friend.dto.FriendResponseDto;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.dto.ActivityRecordResponseDto;
import com.dnd.ground.domain.user.dto.HomeResponseDto;
import com.dnd.ground.domain.user.dto.UserRequestDto;
import com.dnd.ground.domain.user.dto.UserResponseDto;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

/**
 * @description 회원 서비스 인터페이스
 * @author  박세헌, 박찬호
 * @since   2022-08-01
 * @updated 2022-08-26 / 컨트롤러-서비스단 전달 형태 변경 - 박세헌
 */

public interface UserService {
    User save(User user);
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
}
