package com.dnd.ground.domain.user.service;

import com.dnd.ground.domain.exerciseRecord.dto.RecordResponseDto;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.dto.ActivityRecordResponseDto;
import com.dnd.ground.domain.user.dto.HomeResponseDto;
import com.dnd.ground.domain.user.dto.JwtUserDto;
import com.dnd.ground.domain.user.dto.UserResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;

/**
 * @description 회원 서비스 인터페이스
 * @author  박세헌, 박찬호
 * @since   2022-08-01
 * @updated loadUserByUsername함수 추가
 *           - 2022-08-24 박세헌
 */

public interface UserService {
    User save(JwtUserDto user);
    HomeResponseDto showHome(String nickname);
    UserResponseDto.UInfo getUserInfo(String nickname);

    UserResponseDto.Profile getUserProfile(String userNickname, String friendNickname);
    ActivityRecordResponseDto getActivityRecord(String nickname, LocalDateTime start, LocalDateTime end);
    RecordResponseDto.EInfo getExerciseInfo(Long exerciseId);
    UserResponseDto.DetailMap getDetailMap(Long recordId);

    HttpStatus changeFilterMine(String nickname);
    HttpStatus changeFilterFriend(String nickname);
    HttpStatus changeFilterRecord(String nickname);

    ResponseEntity<?> editRecordMessage(Long recordId, String message);
    ResponseEntity<?> editUserProfile(String originNick, String editNick, String intro);

    UserDetails loadUserByUsername(String nickname);
}
