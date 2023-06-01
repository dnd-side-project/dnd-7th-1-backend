package com.dnd.ground.domain.user.controller;

import com.dnd.ground.domain.exerciseRecord.dto.RecordRequestDto;
import com.dnd.ground.domain.exerciseRecord.dto.RecordResponseDto;
import com.dnd.ground.domain.friend.dto.FriendResponseDto;
import com.dnd.ground.domain.user.dto.HomeResponseDto;
import com.dnd.ground.domain.user.dto.UserRequestDto;
import com.dnd.ground.domain.user.dto.UserResponseDto;
import com.dnd.ground.domain.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @description 회원 관련 컨트롤러 구현체
 * @author  박세헌, 박찬호
 * @since   2022-08-02
 * @updated 1.친구 추천 목록 제외 필터 변경 API 구현
 *          - 2023-05-23 박찬호
 */

@Api(tags = "회원")
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user")
@RestController
public class UserControllerImpl implements UserController {

    private final UserService userService;

    @GetMapping("/home")
    @Operation(summary = "홈 화면 조회",
            description = "닉네임을 통해 홈화면에 필요한 유저 정보(userMatrices)\n" +
                    "나와 챌린지를 안하는 친구 정보(friendMatrices, 리스트)\n" +
                    "나와 챌린지를 하는 유저 정보(challengeMatrices, 리스트) 조회")
    public ResponseEntity<HomeResponseDto> home(@ModelAttribute UserRequestDto.Home request){
        return ResponseEntity.ok(userService.showHome(request));
    }

    @GetMapping("/info")
    @Operation(summary = "회원 정보 조회(마이페이지)", description = "회원의 닉네임, 소개 메시지 정보")
    public ResponseEntity<UserResponseDto.MyPage> getUserInfo(@RequestParam("nickname") String nickname) {
        return ResponseEntity.ok().body(userService.getUserInfo(nickname));
    }

    @GetMapping("/profile")
    @Operation(summary = "프로필 조회", description = "회원의 닉네임, 소개 메시지 정보")
    public ResponseEntity<FriendResponseDto.FriendProfile> getUserProfile(
                            @ApiParam(value = "회원 닉네임", required = true) @RequestParam("user") String userNickname,
                            @ApiParam(value = "대상 닉네임", required = true) @RequestParam("friend") String friendNickname) {

        return ResponseEntity.ok().body(userService.getUserProfile(userNickname, friendNickname));
    }

    @GetMapping("/info/activity")
    @Operation(summary = "나의 활동 기록 조회",
            description = "해당 유저의 start-end(기간) 사이 활동기록 조회\n" +
                    "start: 해당 날짜의 00시 00분 00초\n" +
                    "end: 해당 날짜의 23시 59분 59초")
    public ResponseEntity<UserResponseDto.ActivityRecordResponseDto> getActivityRecord(@ModelAttribute UserRequestDto.LookUp requestDto){
        return ResponseEntity.ok().body(userService.getActivityRecord(requestDto));
    }

    @GetMapping("/info/activity/record")
    @Operation(summary = "운동 기록 정보", description = "기록 id를 받아 선택한 운동기록에 대한 정보 조회")
    public ResponseEntity<RecordResponseDto.EInfo> getRecordInfo(@RequestParam("recordId") Long recordId){
        return ResponseEntity.ok().body(userService.getExerciseInfo(recordId));
    }

    @GetMapping("/info/activity/record/map")
    @Operation(summary = "상세 지도", description = "기록 id를 받아 나의 활동 기록에서 해당 기록의 상세 지도 조회")
    public ResponseEntity<UserResponseDto.DetailMap> getDetailMap(@RequestParam("recordId") Long recordId){
        return ResponseEntity.ok().body(userService.getDetailMap(recordId));
    }

    @PostMapping("/filter/mine")
    @Operation(summary = "필터 변경: 나의 기록 보기", description = "'나의 기록 보기' 옵션이 변경됩니다.(True<->False)")
    public ResponseEntity<Boolean> changeFilterMine(@RequestParam("nickname") String nickname) {
        return ResponseEntity.ok().body(userService.changeFilterMine(nickname));
    }

    @PostMapping("/filter/friend")
    @Operation(summary = "필터 변경: 친구 보기", description = "'친구 보기' 옵션이 변경됩니다.(True<->False)")
    public ResponseEntity<Boolean> changeFilterFriend(@RequestParam("nickname") String nickname) {
        return ResponseEntity.ok().body(userService.changeFilterFriend(nickname));
    }

    @PostMapping("/filter/record")
    @Operation(summary = "필터 변경: 친구들에게 보이기", description = "'친구들에게 보이기' 옵션이 변경됩니다.(True<->False)")
    public ResponseEntity<Boolean> changeFilterRecord(@RequestParam("nickname") String nickname) {
        return ResponseEntity.ok().body(userService.changeFilterRecord(nickname));
    }

    @PostMapping("/filter/notification")
    @Operation(summary = "필터 변경: 알람 옵션 변경", description = "알람 종류에 따라 옵션이 ON/OFF됩니다.")
    public ResponseEntity<Boolean> changeFilterNotification(@RequestBody UserRequestDto.NotificationFilter request) {
        return ResponseEntity.ok().body(userService.changeFilterNotification(request));
    }

    @PostMapping("/filter/recommend/friend")
    @Operation(summary = "필터 변경: 친구 추천 제외", description = "'친구 추천 목록 제외' 옵션이 변경됩니다.")
    public ResponseEntity<Boolean> changeFilterRecommendFriend(@RequestParam("nickname") String nickname) {
        return ResponseEntity.ok().body(userService.changeFilterExceptRecommend(nickname));
    }

    @GetMapping("/filter/notification")
    @Operation(summary = "회원의 알람 옵션 조회", description = "회원의 현재 옵션 정보를 반환합니다.")
    public ResponseEntity<UserResponseDto.NotificationFilters> getNotificationFilters(@RequestParam("nickname") String nickname) {
        return ResponseEntity.ok().body(userService.getNotificationFilters(nickname));
    }

    @PostMapping(value = "/info/profile/edit", consumes =  {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "유저 프로필 수정", description = "닉네임, 자기소개, 프로필 사진 수정")
    public ResponseEntity<UserResponseDto.UInfo> editUserProfile(@RequestPart(value = "picture", required = false) MultipartFile picture,
                                                                 @RequestParam(value = "originNickname") String originNickname,
                                                                 @RequestParam(value = "editNickname") String editNickname,
                                                                 @RequestParam(value = "intro") String intro,
                                                                 @RequestParam(value = "isBasic") Boolean isBasic) {
        return ResponseEntity.ok().body(userService.editUserProfile(picture, new UserRequestDto.Profile(originNickname, editNickname, intro, isBasic)));
    }

    @GetMapping("/info/profile/picture")
    @Operation(summary = "회원 프로필 사진 조회", description = "회원의 프로필 사진 조회")
    public ResponseEntity<UserResponseDto.UInfo> getPicture(@RequestParam("nickname") String nickname) {
        return ResponseEntity.ok().body(userService.getPicture(nickname));
    }

    @PostMapping("/info/activity/record/edit")
    @Operation(summary = "운동 기록 메시지 수정", description = "운동 기록 메시지 수정")
    public ResponseEntity<Boolean> getDetailMap(@RequestBody RecordRequestDto.Message requestDto){
        return ResponseEntity.ok().body(userService.editRecordMessage(requestDto));
    }

    @GetMapping("/event-list")
    @Operation(summary = "기록이 있는 날짜 조회", description = "기록이 있는 날짜 조회")
    public ResponseEntity<UserResponseDto.dayEventList> getDayEventList(@ModelAttribute UserRequestDto.DayEventList requestDto){
        return ResponseEntity.ok(userService.getDayEventList(requestDto));
    }

    @GetMapping("/info/profile")
    @Operation(summary = "내 프로필 조회(마이페이지)", description = "내 프로필 조회\n변경된 닉네임 중복 시 DUPLICATE_NICKNAME 예외 전달")
    public ResponseEntity<UserResponseDto.Profile> getMyProfile(@RequestParam String nickname){
        return ResponseEntity.ok(userService.getUserProfile(nickname));
    }
}
