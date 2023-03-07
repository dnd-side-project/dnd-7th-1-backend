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
 * @updated 1.컨트롤러와 서비스 레이어 역할 분리를 위한 일부 메소드 반환타입 변경
 *          - 2023-03-07 박찬호
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

    Boolean editRecordMessage(RecordRequestDto.Message requestDto);
    UserResponseDto.UInfo editUserProfile(MultipartFile multipartFile, UserRequestDto.Profile requestDto);

    UserResponseDto.dayEventList getDayEventList(UserRequestDto.DayEventList requestDto);

    UserResponseDto.Profile getUserProfile(String nickname);
}
