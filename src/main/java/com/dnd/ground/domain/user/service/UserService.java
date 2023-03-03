package com.dnd.ground.domain.user.service;

import com.dnd.ground.domain.exerciseRecord.dto.RecordRequestDto;
import com.dnd.ground.domain.exerciseRecord.dto.RecordResponseDto;
import com.dnd.ground.domain.friend.dto.FriendResponseDto;
import com.dnd.ground.domain.user.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * @description 회원 서비스 인터페이스
 * @author  박세헌, 박찬호
 * @since   2022-08-01
 * @updated 1.회원 영역 데이터 조회 시 일부 영역 내 데이터만 조회하도록 수정
 *          - 2023-02-14 박찬호
 */

public interface UserService {
    HomeResponseDto showHome(UserRequestDto.Home request);
    UserResponseDto.MyPage getUserInfo(String nickname);

    FriendResponseDto.FriendProfile getUserProfile(String userNickname, String friendNickname);
    UserResponseDto.ActivityRecordResponseDto getActivityRecord(UserRequestDto.LookUp requestDto);
    RecordResponseDto.EInfo getExerciseInfo(Long exerciseId);
    UserResponseDto.DetailMap getDetailMap(Long recordId);

    Boolean changeFilterMine(String nickname);
    Boolean changeFilterFriend(String nickname);
    Boolean changeFilterRecord(String nickname);

    ResponseEntity<Boolean> editRecordMessage(RecordRequestDto.Message requestDto);
    ResponseEntity<UserResponseDto.UInfo> editUserProfile(MultipartFile multipartFile, UserRequestDto.Profile requestDto);

    UserResponseDto.dayEventList getDayEventList(UserRequestDto.DayEventList requestDto);

    UserResponseDto.Profile getUserProfile(String nickname);
}
