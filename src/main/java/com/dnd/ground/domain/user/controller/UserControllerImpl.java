package com.dnd.ground.domain.user.controller;

import com.dnd.ground.domain.exerciseRecord.dto.RecordRequestDto;
import com.dnd.ground.domain.exerciseRecord.dto.RecordResponseDto;
import com.dnd.ground.domain.friend.dto.FriendResponseDto;
import com.dnd.ground.domain.user.dto.ActivityRecordResponseDto;
import com.dnd.ground.domain.user.dto.HomeResponseDto;
import com.dnd.ground.domain.user.dto.UserRequestDto;
import com.dnd.ground.domain.user.dto.UserResponseDto;
import com.dnd.ground.domain.user.service.UserService;
import com.dnd.ground.global.util.JwtUtil;
import com.dnd.ground.global.util.JwtVerifyResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @description 회원 관련 컨트롤러 구현체
 * @author  박세헌, 박찬호
 * @since   2022-08-02
 * @updated 2022-09-02 / 온보딩 진입시 호출 함수
 *
 */

@Api(tags = "유저")
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
    public ResponseEntity<HomeResponseDto> home(@RequestParam("nickname") String nickName){
        return ResponseEntity.ok(userService.showHome(nickName));
    }

    @GetMapping("/info")
    @Operation(summary = "회원 정보 조회(마이페이지)", description = "회원의 닉네임, 소개 메시지 정보 (추후 프로필 등 추가 예정)")
    public ResponseEntity<UserResponseDto.Profile> getUserInfo(@RequestParam("nickname") String nickname) {
        return ResponseEntity.ok().body(userService.getUserInfo(nickname));
    }

    @GetMapping("/profile")
    @Operation(summary = "프로필 조회", description = "회원의 닉네임, 소개 메시지 정보 (추후 프로필 등 추가 예정)")
    public ResponseEntity<FriendResponseDto.FriendProfile> getUserProfile(
                            @ApiParam(value = "회원 닉네임", required = true) @RequestParam("user") String userNickname,
                            @ApiParam(value = "대상 닉네임", required = true) @RequestParam("friend") String friendNickname) {

        return ResponseEntity.ok().body(userService.getUserProfile(userNickname, friendNickname));
    }

    @PostMapping("/info/activity")
    @Operation(summary = "나의 활동 기록 조회",
            description = "해당 유저의 start-end(기간) 사이 활동기록 조회\n" +
                    "start: 해당 날짜의 00시 00분 00초\n" +
                    "end: 해당 날짜의 23시 59분 59초")
    public ResponseEntity<ActivityRecordResponseDto> getActivityRecord(@RequestBody UserRequestDto.LookUp requestDto){
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

    @PostMapping("filter/mine")
    @Operation(summary = "필터 변경: 나의 기록 보기", description = "'나의 기록 보기' 옵션이 변경됩니다.(True<->False)")
    public ResponseEntity<Boolean> changeFilterMine(@RequestParam("nickname") String nickname) {
        return ResponseEntity.ok().body(userService.changeFilterMine(nickname));
    }

    @PostMapping("filter/friend")
    @Operation(summary = "필터 변경: 친구 보기", description = "'친구 보기' 옵션이 변경됩니다.(True<->False)")
    public ResponseEntity<Boolean> changeFilterFriend(@RequestParam("nickname") String nickname) {
        return ResponseEntity.ok().body(userService.changeFilterFriend(nickname));
    }

    @PostMapping("filter/record")
    @Operation(summary = "필터 변경: 친구들에게 보이기", description = "'친구들에게 보이기' 옵션이 변경됩니다.(True<->False)")
    public ResponseEntity<Boolean> changeFilterRecord(@RequestParam("nickname") String nickname) {
        return ResponseEntity.ok().body(userService.changeFilterRecord(nickname));
    }

    @PostMapping("/info/profile/edit")
    @Operation(summary = "유저 프로필 수정", description = "닉네임, 자기소개 수정(예외 처리 예정)")
    public ResponseEntity<Boolean> editUserProfile(@RequestBody UserRequestDto.Profile requestDto){
        /* 닉네임이 비어있을때 예외 처리 필요 */
        /* 닉네임이 중복일때 예외 처리 필요 */
        return userService.editUserProfile(requestDto);
    }

    @PostMapping("/info/activity/record/edit")
    @Operation(summary = "운동 기록 메시지 수정", description = "운동 기록 메시지 수정")
    public ResponseEntity<Boolean> getDetailMap(@RequestBody RecordRequestDto.Message requestDto){
        return userService.editRecordMessage(requestDto);
    }

    /*
    - 클라가 앱에 처음 진입했을때 액세스 토큰이 있다면 토큰과 함께 해당 uri로 호출
    - (JWTCheckFilter를 거친 후 닉네임 반환)
    - 토큰이 없다면 카카오 로그인 페이지로 가야함
    */
    @GetMapping("/main")
    public ResponseEntity<?> main(HttpServletRequest request){
        return userService.showMain(request);
    }
}
