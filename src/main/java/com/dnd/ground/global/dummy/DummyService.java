package com.dnd.ground.global.dummy;

import com.dnd.ground.domain.challenge.Challenge;
import com.dnd.ground.domain.challenge.UserChallenge;
import com.dnd.ground.domain.challenge.repository.ChallengeRepository;
import com.dnd.ground.domain.challenge.repository.UserChallengeRepository;
import com.dnd.ground.domain.exerciseRecord.ExerciseRecord;
import com.dnd.ground.domain.exerciseRecord.Repository.ExerciseRecordRepository;
import com.dnd.ground.domain.friend.Friend;
import com.dnd.ground.domain.friend.repository.FriendRepository;
import com.dnd.ground.domain.matrix.Matrix;
import com.dnd.ground.domain.matrix.repository.MatrixRepository;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.repository.UserRepository;
import com.dnd.ground.global.exception.*;
import com.dnd.ground.global.util.JwtUtil;
import com.dnd.ground.global.util.UuidUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @description 더미 데이터 생성을 위한 서비스
 * @author  박찬호
 * @since   2022-10-04
 * @updated 1.챌린지 uuid 조회
 *          2.챌린지 상태 변경
 *          3.UC 상태 변경
 *          - 2022.11.23 박찬호
 */

@RequiredArgsConstructor
@Service
public class DummyService {
    private final UserRepository userRepository;
    private final ExerciseRecordRepository exerciseRecordRepository;
    private final MatrixRepository matrixRepository;
    private final FriendRepository friendRepository;
    private final UserChallengeRepository userChallengeRepository;
    private final ChallengeRepository challengeRepository;

    /*더미 유저 조회*/
    public ResponseEntity<?> getDummyUser(String nickname) {
        User user = userRepository.findByNickname(nickname).orElseThrow(
                () -> new UserException(ExceptionCodeSet.USER_NOT_FOUND)
        );

        return ResponseEntity.ok()
                .body(DummyResponseDto.DummyUser.builder()
                        .nickname(nickname)
                        .created(user.getCreated())
                        .intro(user.getIntro())
                        .mail(user.getEmail())
                        .latitude(user.getLatitude())
                        .longitude(user.getLongitude())
                        .isShowMine(user.getIsShowMine())
                        .isShowFriend(user.getIsShowFriend())
                        .isPublicRecord(user.getIsPublicRecord())
                        .pictureName(user.getPictureName())
                        .picturePath(user.getPicturePath())
                        .build()
                );
    }

    /*더미 유저 생성*/
    @Transactional
    public ResponseEntity<?> createDummyUser(DummyRequestDto.DummyUser request) {

        String accessToken = JwtUtil.createAccessToken(request.getNickname(), LocalDateTime.now());
        String refreshToken = JwtUtil.createRefreshToken(request.getNickname(), LocalDateTime.now());

        try {
            userRepository.save(
                    User.builder()
                            .nickname(request.getNickname())
                            .email(request.getMail())
                            .created(LocalDateTime.now())
                            .intro(request.getIntro())
                            .latitude(null)
                            .longitude(null)
                            .isShowMine(true)
                            .isShowFriend(true)
                            .isPublicRecord(true)
                            .pictureName("user/profile/default_profile.png")
                            .picturePath("https://dnd-ground-bucket.s3.ap-northeast-2.amazonaws.com/user/profile/default_profile.png")
                            .build()
            );
        } catch (Exception e) { //DataIntegrityViolationException가 발생해야되는데, 롤백되면서 catch문을 타지 않음..
            return ResponseEntity.badRequest()
                    .body("중복된 닉네임, 이메일이 존재합니다.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Refresh-Token", "Bearer " + refreshToken);

        return ResponseEntity.ok()
                .headers(headers)
                .body(true);

    }

    /*더미 회원 삭제*/
    @Transactional
    public ResponseEntity<?> deleteDummyUser(String nickname) {

        User user = userRepository.findByNickname(nickname).orElseThrow(
                () -> new UserException(ExceptionCodeSet.USER_NOT_FOUND)
        );


        //회원의 운동 기록 삭제
        List<ExerciseRecord> records = exerciseRecordRepository.findRecordsByUser(user);

        for (ExerciseRecord record : records) {
            //운동 기록 삭제를 위해 matrix 삭제
            List<Matrix> matrices = matrixRepository.findByRecord(record);
            matrixRepository.deleteAll(matrices);
            exerciseRecordRepository.delete(record);
        }

        //친구 관계 삭제
        List<Friend> friends = friendRepository.findFriendsAnyway(user);
        friendRepository.deleteAll(friends);

        //챌린지-회원 관계 테이블 삭제
        List<UserChallenge> userChallenges = userChallengeRepository.findUCs(user);
        userChallengeRepository.deleteAll(userChallenges);

        //회원 삭제
        userRepository.deleteByNickname(nickname);
        return ResponseEntity.ok()
                .body(true);
    }

    /*운동 기록 조회*/
    public ResponseEntity<?> getDummyRecords(String nickname) {
        User user = userRepository.findByNickname(nickname).orElseThrow(
                () -> new UserException(ExceptionCodeSet.USER_NOT_FOUND)
        );

        List<ExerciseRecord> records = exerciseRecordRepository.findRecordsByUser(user);

        List<DummyResponseDto.DummyRecords> result = new ArrayList<>();

        for (ExerciseRecord record : records) {
            result.add(
                    DummyResponseDto.DummyRecords.builder()
                            .id(record.getId())
                            .started(record.getStarted())
                            .ended(record.getEnded())
                            .exerciseTime(record.getExerciseTime())
                            .distance(record.getDistance())
                            .stepCount(record.getStepCount())
                            .message(record.getMessage())
                            .build()
            );
        }

        return ResponseEntity.ok()
                .body(result);
    }

    /* 더미 운동 기록 생성 */
    @Transactional
    public ResponseEntity<?> createDummyRecord(DummyRequestDto.DummyRecord request) {

        User user = userRepository.findByNickname(request.getNickname()).orElseThrow(
                () -> new UserException(ExceptionCodeSet.USER_NOT_FOUND)
        );

        //운동 기록 생성
        ExerciseRecord record = ExerciseRecord.builder()
                .user(user)
                .started(request.getStarted())
                .ended(request.getEnded())
                .distance(request.getDistance())
                .exerciseTime(request.getExerciseTime())
                .stepCount(request.getStepCount())
                .message(request.getMessage())
                .matrices(new ArrayList<>())
                .build();

        //영억 넣기
        ArrayList<ArrayList<Double>> matrices = request.getMatrices();
        for (ArrayList<Double> matrix : matrices) {
            record.addMatrix(new Matrix(matrix.get(0), matrix.get(1)));
        }

        exerciseRecordRepository.save(record);

        return ResponseEntity.ok()
                .body(record.getId());
    }

    /*운동 기록 추가*/
    @Transactional
    public ResponseEntity<?> insertDummyMatrix(DummyRequestDto.DummyRecordMatrix request) {
        ExerciseRecord record = exerciseRecordRepository.findById(request.getRecordId()).orElseThrow(
                () -> new ExerciseRecordException(ExceptionCodeSet.RECORD_NOT_FOUND)
        );

        ArrayList<ArrayList<Double>> matrices = request.getMatrices();

        for (ArrayList<Double> matrix : matrices) {
            record.addMatrix(new Matrix(matrix.get(0), matrix.get(1)));
        }

        return ResponseEntity.ok()
                .body(matrices.size());
    }

    /*영역 조회*/
    public ResponseEntity<?> getDummyMatrices(Long recordId) {
        ExerciseRecord record = exerciseRecordRepository.findById(recordId).orElseThrow(
                () -> new ExerciseRecordException(ExceptionCodeSet.RECORD_NOT_FOUND)
        );

        DummyResponseDto.DummyMatricesInfo response = new DummyResponseDto.DummyMatricesInfo();

        List<Matrix> matrices = matrixRepository.findByRecord(record);

        //사이즈
        response.setSize((long) matrices.size());

        //영역 추가
        for (Matrix matrix : matrices) {
            response.addMatrix(matrix.getLatitude(), matrix.getLongitude());
        }

        return ResponseEntity.ok()
                .body(response);
    }

    /*운동 기록 삭제*/
    @Transactional
    public ResponseEntity<?> deleteDummyRecord(Long recordId) {
        ExerciseRecord record = exerciseRecordRepository.findById(recordId).orElseThrow(
                () -> new ExerciseRecordException(ExceptionCodeSet.RECORD_NOT_FOUND)
        );

        List<Matrix> matrices = matrixRepository.findByRecord(record);
        matrixRepository.deleteAll(matrices);
        exerciseRecordRepository.delete(record);

        return ResponseEntity.ok()
                .body(true);
    }

    /*챌린지 상태 변경*/
    @Transactional
    public ResponseEntity<?> changeChallengeStatus(DummyRequestDto.DummyChallengeStatus request) {
        Challenge challenge = challengeRepository.findByUuid(UuidUtil.hexToBytes(request.getUuid()))
                .orElseThrow(() -> new ChallengeException(ExceptionCodeSet.CHALLENGE_NOT_FOUND)
        );

        challenge.updateStatus(request.getStatus());

        return ResponseEntity.ok(true);
    }

    /*챌린지에 참여하는 회원의 상태 변경*/
    @Transactional
    public ResponseEntity<?> changeUCStatus(DummyRequestDto.DummyUCStatus request) {
        User user = userRepository.findByNickname(request.getNickname()).orElseThrow(
                () -> new UserException(ExceptionCodeSet.USER_NOT_FOUND)
        );

        Challenge challenge = challengeRepository.findByUuid(UuidUtil.hexToBytes(request.getUuid()))
                .orElseThrow(() -> new ChallengeException(ExceptionCodeSet.CHALLENGE_NOT_FOUND)
        );

        UserChallenge userChallenge = userChallengeRepository.findByUserAndChallenge(user, challenge).orElseThrow(
                () -> new ChallengeException(ExceptionCodeSet.USER_CHALLENGE_NOT_FOUND, user.getNickname())
        );

        userChallenge.changeStatus(request.getStatus());

        return ResponseEntity.ok(true);
    }
}
