package com.dnd.ground.domain.user.controller;

import com.dnd.ground.domain.exerciseRecord.dto.RecordRequestDto;
import com.dnd.ground.domain.exerciseRecord.dto.RecordResponseDto;
import com.dnd.ground.domain.friend.dto.FriendResponseDto;
import com.dnd.ground.domain.user.dto.UserRequestDto;
import com.dnd.ground.domain.user.dto.UserResponseDto;
import io.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;


/**
 * @description 회원 관련 역할 분리 인터페이스
 * @author  박세헌, 박찬호
 * @since   2022-08-02
 * @updated 운동 기록 날짜 조회 api
 *          - 2022-09-24 박세헌
 */

public interface UserController {
    ResponseEntity<?> home(@RequestParam("nickName") String nickName);
    ResponseEntity<UserResponseDto.MyPage> getUserInfo(@RequestParam("nickname") String nickname);
    ResponseEntity<FriendResponseDto.FriendProfile> getUserProfile(
            @ApiParam(value = "회원 닉네임", required = true) @RequestParam("user") String userNickname,
            @ApiParam(value = "대상 닉네임", required = true) @RequestParam("friend") String friendNickname);

    ResponseEntity<UserResponseDto.ActivityRecordResponseDto> getActivityRecord(@RequestBody UserRequestDto.LookUp requestDto);
    ResponseEntity<RecordResponseDto.EInfo> getRecordInfo(@RequestParam("recordId") Long recordId);
    ResponseEntity<UserResponseDto.DetailMap> getDetailMap(@RequestParam("recordId") Long recordId);

    ResponseEntity<Boolean> changeFilterMine(@RequestParam("nickname") String nickname);
    ResponseEntity<Boolean> changeFilterFriend(@RequestParam("nickname") String nickname);
    ResponseEntity<Boolean> changeFilterRecord(@RequestParam("nickname") String nickname);

    ResponseEntity<Boolean> editUserProfile(@RequestPart(required = false) MultipartFile multipartFile,
                                            @RequestPart UserRequestDto.Profile requestDto);

    ResponseEntity<Boolean> getDetailMap(@RequestBody RecordRequestDto.Message requestDto);

    ResponseEntity<UserResponseDto.dayEventList> getDayEventList(@RequestBody UserRequestDto.dayEventList requestDto);

    ResponseEntity<UserResponseDto.Profile> getMyProfile(@RequestParam String nickname);
}
