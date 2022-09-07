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
 * @updated 회원 인증/인가 및 로그인 관련 메소드 이동(UserService -> AuthService)
 *          2022-09-07 박찬호
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
}
