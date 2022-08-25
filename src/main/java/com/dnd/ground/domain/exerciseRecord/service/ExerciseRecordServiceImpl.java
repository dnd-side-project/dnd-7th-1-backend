package com.dnd.ground.domain.exerciseRecord.service;

import com.dnd.ground.domain.challenge.Challenge;
import com.dnd.ground.domain.challenge.ChallengeColor;
import com.dnd.ground.domain.challenge.repository.ChallengeRepository;
import com.dnd.ground.domain.challenge.repository.UserChallengeRepository;
import com.dnd.ground.domain.exerciseRecord.ExerciseRecord;
import com.dnd.ground.domain.exerciseRecord.Repository.ExerciseRecordRepository;
import com.dnd.ground.domain.exerciseRecord.dto.EndRequestDto;
import com.dnd.ground.domain.friend.service.FriendService;
import com.dnd.ground.domain.matrix.Matrix;
import com.dnd.ground.domain.matrix.dto.MatrixDto;
import com.dnd.ground.domain.matrix.matrixRepository.MatrixRepository;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.dto.HomeResponseDto;
import com.dnd.ground.domain.user.dto.RankResponseDto;
import com.dnd.ground.domain.user.dto.UserResponseDto;
import com.dnd.ground.domain.user.repository.UserRepository;
import com.dnd.ground.global.exception.CNotFoundException;
import com.dnd.ground.global.exception.CommonErrorCode;
import lombok.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Tuple;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @description 운동 기록 서비스 클래스
 * @author  박세헌
 * @since   2022-08-01
 * @updated 2022-08-24 / 1. orElseThrow() 예외 처리 - 박찬호
 *                       2. 랭킹 동점 로직, 유저 맨위
 *                       3. 기록 시작 api 반환 형태 수정 - 박세헌
 */

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExerciseRecordServiceImpl implements ExerciseRecordService {

    private final ExerciseRecordRepository exerciseRecordRepository;
    private final UserRepository userRepository;
    private final MatrixRepository matrixRepository;
    private final FriendService friendService;
    private final ChallengeRepository challengeRepository;
    private final UserChallengeRepository userChallengeRepository;

    @Transactional
    public void delete(Long exerciseRecordId) {
        exerciseRecordRepository.deleteById(exerciseRecordId);
    }

    // 기록 시작
    // 운동기록 id, 일주일 누적 영역 반환
    public HomeResponseDto recordStart(String nickname){
        User user = userRepository.findByNickname(nickname).orElseThrow(
                () -> new CNotFoundException(CommonErrorCode.NOT_FOUND_USER));

        /*회원의 matrix 와 정보 (userMatrix)*/
        UserResponseDto.UserMatrix userMatrix = new UserResponseDto.UserMatrix(user);

        List<ExerciseRecord> userRecordOfThisWeek = exerciseRecordRepository.findRecordOfThisWeek(user.getId()); // 이번주 운동기록 조회
        List<MatrixDto> userMatrixSet = matrixRepository.findMatrixSetByRecords(userRecordOfThisWeek);  // 운동 기록의 영역 조회

        //회원 정보 저장
        userMatrix.setProperties(user.getNickname(), userMatrixSet.size(), userMatrixSet, null, null);

        /*----------*/
        //진행 중인 챌린지 목록 조회 List<UserChallenge>
        List<Challenge> challenges = challengeRepository.findProgressChallenge(user);

        //챌린지를 함께하지 않는 친구 목록
        List<User> friendsNotChallenge = friendService.getFriends(user);

        //나랑 챌린지를 함께 하는 사람들(친구+친구X 둘 다)
        Set<User> friendsWithChallenge = new HashSet<>();

        for (Challenge challenge : challenges) {
            List<User> challengeUsers = userChallengeRepository.findChallengeUsers(challenge);
            //챌린지를 함께하고 있는 사람들 조회
            for (User cu : challengeUsers) {
                friendsWithChallenge.add(cu);
                friendsNotChallenge.remove(cu);
            }
        }
        friendsWithChallenge.remove(user);
        /*----------*/

        /*챌린지를 안하는 친구들의 matrix 와 정보 (friendMatrices)*/
        Map<String, List<MatrixDto>> friendHashMap= new HashMap<>();
        List<UserResponseDto.FriendMatrix> friendMatrices = new ArrayList<>();

        friendsNotChallenge.forEach(nf -> friendHashMap.put(nf.getNickname(),
                matrixRepository.findMatrixSetByRecords(exerciseRecordRepository.findRecordOfThisWeek(nf.getId()))));  // 이번주 운동기록 조회하여 영역 대입

        for (String friendNickname : friendHashMap.keySet()) {
            User friend = userRepository.findByNickname(friendNickname).orElseThrow(
                    () -> new CNotFoundException(CommonErrorCode.NOT_FOUND_USER));

            friendMatrices.add(new UserResponseDto.FriendMatrix(friendNickname, friend.getLatitude(), friend.getLongitude(),
                    friendHashMap.get(friendNickname)));
        }

        /*챌린지를 하는 사람들의 matrix 와 정보 (challengeMatrices)*/
        List<UserResponseDto.ChallengeMatrix> challengeMatrices = new ArrayList<>();

        for (User friend : friendsWithChallenge) {
            Integer challengeNumber = challengeRepository.findCountChallenge(user, friend); // 함께하는 챌린지 수

            //색깔 처리
            Challenge challengeWithFriend = challengeRepository.findChallengesWithFriend(user, friend).get(0);//함께하는 첫번째 챌린지 조회
            ChallengeColor challengeColor = userChallengeRepository.findChallengeColor(user, challengeWithFriend);//회원 기준 해당 챌린지 색깔

            List<ExerciseRecord> challengeRecordOfThisWeek = exerciseRecordRepository.findRecordOfThisWeek(friend.getId()); // 이번주 운동기록 조회
            List<MatrixDto> challengeMatrixSetDto = matrixRepository.findMatrixSetByRecords(challengeRecordOfThisWeek); // 운동 기록의 영역 조회

            challengeMatrices.add(
                    new UserResponseDto.ChallengeMatrix(
                            friend.getNickname(), challengeNumber, challengeColor,
                            friend.getLatitude(), friend.getLongitude(), challengeMatrixSetDto)
            );
        }

        return HomeResponseDto.builder()
                .userMatrices(userMatrix)
                .friendMatrices(friendMatrices)
                .challengeMatrices(challengeMatrices)
                .challengesNumber(null)
                .isShowMine(user.getIsShowMine())
                .isShowFriend(user.getIsShowFriend())
                .isPublicRecord(user.getIsPublicRecord())
                .build();
    }

    // 기록 끝
    @Transactional
    public ResponseEntity<?> recordEnd(EndRequestDto endRequestDto) {
        // 유저 찾아서 운동 기록 생성

        User user = userRepository.findByNickname(endRequestDto.getNickname()).orElseThrow(
                () -> new CNotFoundException(CommonErrorCode.NOT_FOUND_USER));
        ExerciseRecord exerciseRecord = new ExerciseRecord(user);

        // 정보 update(ended, 거리, 걸음수, 운동시간, 상세 기록, 시작 시간, 끝 시간)
        exerciseRecord.updateInfo(endRequestDto.getDistance(), endRequestDto.getStepCount(),
                endRequestDto.getExerciseTime(), endRequestDto.getMessage(), endRequestDto.getStarted(), endRequestDto.getEnded());

        //영역 저장
        ArrayList<ArrayList<Double>> matrices = endRequestDto.getMatrices();
        for (ArrayList<Double> matrix : matrices) {
            exerciseRecord.addMatrix(new Matrix(matrix.get(0), matrix.get(1)));
        }

        //회원 마지막 위치 최신화
        ArrayList<Double> lastPosition = matrices.get(matrices.size() - 1);
        exerciseRecord.getUser().updatePosition(lastPosition.get(0), lastPosition.get(1));

        exerciseRecordRepository.save(exerciseRecord);
        return new ResponseEntity("성공", HttpStatus.CREATED);
    }

    // 랭킹 조회(누적 걸음 수 기준)  (추후 파라미터 Requestdto로 교체 예정)
    public RankResponseDto.Step stepRanking(String nickname, LocalDateTime start, LocalDateTime end) {
        User user = userRepository.findByNickname(nickname).orElseThrow(
                () -> new CNotFoundException(CommonErrorCode.NOT_FOUND_USER));

        List<User> userAndFriends = friendService.getFriends(user);  // 친구들 조회
        userAndFriends.add(0, user);  // 유저 추가
        List<UserResponseDto.Ranking> stepRankings = new ArrayList<>(); // [랭킹, 닉네임, 걸음 수]

        // [Tuple(닉네임, 걸음 수)] 걸음 수 기준 내림차순 정렬
        List<Tuple> stepCount = exerciseRecordRepository.findStepCount(userAndFriends, start, end);

        int count = 0;
        int rank = 1;

        Long matrixNumber = (Long) stepCount.get(0).get(1);  // 맨 처음 user의 걸음 수
        for (Tuple info : stepCount) {
            // 전 유저와 걸음 수가 같다면 랭크 유지
            if (Objects.equals(info.get(1), matrixNumber)) {

                // 유저 찾았으면 저장해둠
                if (Objects.equals(info.get(0), user.getNickname())) {
                    stepRankings.add(0, new UserResponseDto.Ranking(rank, (String) info.get(0),
                            (Long) info.get(1)));
                }

                stepRankings.add(new UserResponseDto.Ranking(rank, (String) info.get(0),
                        (Long) info.get(1)));
                count += 1;
                continue;
            }

            // 전 유저보다 걸음수가 작다면 앞에 있는 사람수 만큼이 자신 랭킹
            count += 1;
            rank = count;

            // 유저 찾았으면 저장해둠
            if (Objects.equals(info.get(0), user.getNickname())) {
                stepRankings.add(0, new UserResponseDto.Ranking(rank, (String) info.get(0),
                        (Long) info.get(1)));
            }
            stepRankings.add(new UserResponseDto.Ranking(rank, (String) info.get(0),
                    (Long) info.get(1)));
            matrixNumber = (Long) info.get(1);  // 걸음 수 update!
        }
        return new RankResponseDto.Step(stepRankings);
    }
}
