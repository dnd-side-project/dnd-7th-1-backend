package com.dnd.ground.domain.user.service;

import com.dnd.ground.domain.exerciseRecord.dto.RecordResponseDto;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.dto.ActivityRecordResponseDto;
import com.dnd.ground.domain.user.dto.HomeResponseDto;
import com.dnd.ground.domain.user.dto.UserResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

/**
 * @description 회원 서비스 인터페이스
 * @author  박세헌, 박찬호
 * @since   2022-08-01
 * @updated  2.필터 변경 Response body 수정 (null -> 변경 값)
 *           - 2022-08-24 박찬호
 */

public interface UserService {
    User save(User user);
    HomeResponseDto showHome(String nickname);
    UserResponseDto.UInfo getUserInfo(String nickname);

    UserResponseDto.Profile getUserProfile(String userNickname, String friendNickname);
    ActivityRecordResponseDto getActivityRecord(String nickname, LocalDateTime start, LocalDateTime end);
    RecordResponseDto.EInfo getExerciseInfo(Long exerciseId);
    UserResponseDto.DetailMap getDetailMap(Long recordId);

    Boolean changeFilterMine(String nickname);
    Boolean changeFilterFriend(String nickname);
    Boolean changeFilterRecord(String nickname);

    ResponseEntity<?> editRecordMessage(Long recordId, String message);
    ResponseEntity<?> editUserProfile(String originNick, String editNick, String intro);
}
