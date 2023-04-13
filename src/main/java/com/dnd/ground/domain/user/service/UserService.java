package com.dnd.ground.domain.user.service;

import com.dnd.ground.domain.exerciseRecord.dto.RecordRequestDto;
import com.dnd.ground.domain.exerciseRecord.dto.RecordResponseDto;
import com.dnd.ground.domain.friend.dto.FriendResponseDto;
import com.dnd.ground.domain.user.dto.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @description 회원 서비스 인터페이스
 * @author  박세헌, 박찬호
 * @since   2022-08-01
 * @updated 1.회원의 푸시 알람 관련 필터 변경을 위한 API 구현
 *          - 2023-04-13 박찬호
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
    Boolean changeFilterNotification(UserRequestDto.NotificationFilter request);

    Boolean editRecordMessage(RecordRequestDto.Message requestDto);
    UserResponseDto.UInfo editUserProfile(MultipartFile multipartFile, UserRequestDto.Profile requestDto);

    UserResponseDto.dayEventList getDayEventList(UserRequestDto.DayEventList requestDto);

    UserResponseDto.Profile getUserProfile(String nickname);
}
