package com.dnd.ground.global.dummy;

import com.dnd.ground.domain.challenge.*;
import com.dnd.ground.domain.challenge.repository.ChallengeRepository;
import com.dnd.ground.domain.challenge.repository.UserChallengeRepository;
import com.dnd.ground.domain.exerciseRecord.ExerciseRecord;
import com.dnd.ground.domain.exerciseRecord.Repository.ExerciseRecordRepository;
import com.dnd.ground.domain.friend.Friend;
import com.dnd.ground.domain.friend.FriendStatus;
import com.dnd.ground.domain.friend.repository.FriendRepository;
import com.dnd.ground.domain.matrix.Matrix;
import com.dnd.ground.domain.user.LoginType;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.dnd.ground.domain.challenge.ChallengeColor.*;
import static com.dnd.ground.domain.challenge.ChallengeStatus.*;
import static com.dnd.ground.domain.challenge.ChallengeType.Accumulate;
import static com.dnd.ground.domain.challenge.ChallengeType.Widen;
import static java.time.DayOfWeek.*;

/**
 * @description Test data 생성
 * @author  박찬호
 * @since   2023-02-07
 * @updated 1. 운동기록 관련 데이터 생성 (ExerciseRecord, Matrix)
 *          2. 친구 관계 일부 생성
 *          - 2023.02.14 박찬호
 */

@Profile("dev")
@RequiredArgsConstructor
@Component
public class InitDataInLocal {
    @Value("${picture.path}")
    private String DEFAULT_PATH;

    @Value("${picture.name}")
    private String DEFAULT_NAME;
    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;
    private final UserChallengeRepository userChallengeRepository;
    private final ExerciseRecordRepository exerciseRecordRepository;
    private final FriendRepository friendRepository;

    private final int CHALLENGE_COUNT = 9;
    private final int USER_COUNT = 20;
    private final User[] users = new User[USER_COUNT+1];
    private final Challenge[] challenges = new Challenge[CHALLENGE_COUNT+1];

    @PostConstruct
    public void init() {
        createUser();
        createChallenge();
        createUserChallengeRelation();
        createExerciseRecordAndMatrix();
        createFriend();
    }

    private int createRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    /**
     * @description 테스트용 회원 생성
     */
    private void createUser() {
        for (int i = 1; i <= 20; i++) {
            double latitude = 34.337542 - i;
            double longitude = -122.041062 - i;
            String alias = String.valueOf((char) (64 + i));

            User user = User.builder()
                    .created(LocalDateTime.now().minusMonths(2).minusDays(i))
                    .email("user_" + alias + "@gmail.com")
                    .intro(alias + "의 intro")
                    .isPublicRecord(i % 2 == 0)
                    .isShowFriend(i % 2 == 0)
                    .isShowMine(i % 2 == 0)
                    .latitude(latitude)
                    .longitude(longitude)
                    .loginType(i % 2 == 0 ? LoginType.APPLE : LoginType.KAKAO)
                    .nickname("Nick" + alias)
                    .picturePath(DEFAULT_PATH)
                    .pictureName(DEFAULT_NAME)
                    .build();

            userRepository.save(user);
            users[i] = user;
        }
    }

    /**
     * @description 테스트용 챌린지 생성
     * 타입 - A: Accumulate | W: Widen
     * 챌린지 미참여 회원 : E, J~T
     * 개수 - 9개
     * 번호    이름       상태           타입  주최자     참가자    생성 날짜                시작 날짜
     * 1      A    이미 완료된 챌린지     A     A      B  C     2주 전                  1주 전
     * 2      B    이미 완료된 챌린지     W     A      B  D     3주 전                  2주 전
     * 3      C    이미 완료된 챌린지     A     F      A  G     4주 전 월요일             4주 전 화요일
     * 4      D    진행 중인 챌린지      W     A      B  C     1주 전                   월요일
     * 5      E    진행 중인 챌린지      A     G      A  H      월요일                   화요일
     * 6      F    진행 중인 챌린지      W     H      C  D      화요일(월요일이면 어제)      수요일(월요일 시작)
     * 7      G    진행 대기 중인 챌린지  A     A      B  C      월요일                   화요일
     * 8      H    진행 대기 중인 챌린지  W     H      D  I      화요일(월요일이면 어제)      수요일(월요일 시작)
     * 9      I    진행 대기 중인 챌린지  W     B      F  G      1주 전                  월요일
     */
    private void createChallenge() {
        LocalDateTime nowTime = LocalDateTime.now();
        LocalDate nowDate = LocalDate.now();
        int today = nowTime.getDayOfWeek().getValue();

        LocalDateTime created = null;
        LocalDate started = null;
        ChallengeStatus status = null;
        ChallengeType type = null;

        for (int i = 1; i <= CHALLENGE_COUNT; i++) {
            String uuid = String.valueOf(i).repeat(32);

            switch (i) {
                case 1:
                    created = nowTime.minusWeeks(i + 1);
                    started = nowDate.minusWeeks(i);
                    status = Done;
                    type = Widen;
                    break;
                case 2:
                    created = nowTime.minusWeeks(i + 1);
                    started = nowDate.minusWeeks(i);
                    status = Done;
                    type = Accumulate;
                    break;
                case 3:
                    created = nowTime.minusWeeks(i + 1).with(MONDAY);
                    started = nowDate.minusWeeks(i + 1).with(DayOfWeek.TUESDAY);
                    status = Done;
                    type = Accumulate;
                    break;
                case 4:
                    created = nowTime.minusWeeks(1);
                    started = nowDate.with(MONDAY);
                    status = Progress;
                    type = Widen;
                    break;
                case 5:
                    created = nowTime.with(MONDAY);
                    started = nowDate.with(TUESDAY);
                    status = Progress;
                    type = Accumulate;
                    break;
                case 6:
                    if (today == 1) { //오늘이 월요일이면 어제 생성, 오늘 시작
                        created = nowTime.minusDays(1);
                        started = nowDate;
                    } else { //아니면 화요일 생성, 수요일 시작
                        created = nowTime.with(TUESDAY);
                        started = nowDate.with(WEDNESDAY);
                    }
                    status = Progress;
                    type = Widen;
                    break;
                case 7:
                    created = nowTime.with(MONDAY);
                    started = nowDate.with(TUESDAY);
                    status = Wait;
                    type = Accumulate;
                    break;
                case 8:
                    if (today == 1) { //오늘이 월요일이면 어제 생성, 오늘 시작
                        created = nowTime.minusDays(1);
                        started = nowDate;
                    } else { //아니면 화요일 생성, 수요일 시작
                        created = nowTime.with(TUESDAY);
                        started = nowDate.with(WEDNESDAY);
                    }
                    status = Wait;
                    type = Widen;
                    break;
                case 9:
                    created = nowTime.minusWeeks(1);
                    started = nowDate.with(MONDAY);
                    status = Wait;
                    type = Accumulate;
                    break;
            }

            Challenge challenge = Challenge.builder()
                    .created(created)
                    .message(i + "번 챌린지")
                    .name("챌린지_" + i)
                    .started(started)
                    .status(status)
                    .type(type)
                    .uuid(uuid)
                    .build();

            challengeRepository.save(challenge);
            challenges[i] = challenge;
        }
    }

    /**
     * @description 테스트용 회원-챌린지 간 관계 데이터 생성
     * 9번 챌린지 2번째 회원은 거절한 상태
     */
    public void createUserChallengeRelation() {
        UserChallenge master;
        UserChallenge member1;
        UserChallenge member2;

        for (int i = 1; i<= CHALLENGE_COUNT; i++) {
            switch (i) {
                case 1:
                    master  = new UserChallenge((long) i, users[1], challenges[i], MasterDone, Red);
                    member1 = new UserChallenge((long) i, users[2], challenges[i], Done, Red);
                    member2 = new UserChallenge((long) i, users[3], challenges[i], Done, Red);

                    userChallengeRepository.save(master);
                    userChallengeRepository.save(member1);
                    userChallengeRepository.save(member2);
                    break;
                case 2:
                    master  = new UserChallenge((long) i, users[1], challenges[i], MasterDone, Red);
                    member1 = new UserChallenge((long) i, users[2], challenges[i], Done, Red);
                    member2 = new UserChallenge((long) i, users[4], challenges[i], Done, Red);

                    userChallengeRepository.save(master);
                    userChallengeRepository.save(member1);
                    userChallengeRepository.save(member2);
                    break;
                case 3:
                    master  = new UserChallenge((long) i, users[6], challenges[i], MasterDone, Red);
                    member1 = new UserChallenge((long) i, users[1], challenges[i], Done, Red);
                    member2 = new UserChallenge((long) i, users[7], challenges[i], Done, Red);

                    userChallengeRepository.save(master);
                    userChallengeRepository.save(member1);
                    userChallengeRepository.save(member2);
                    break;
                case 4:
                    master  = new UserChallenge((long) i, users[1], challenges[i], Master, Red);
                    member1 = new UserChallenge((long) i, users[2], challenges[i], Progress, Red);
                    member2 = new UserChallenge((long) i, users[3], challenges[i], Progress, Red);

                    userChallengeRepository.save(master);
                    userChallengeRepository.save(member1);
                    userChallengeRepository.save(member2);
                    break;
                case 5:
                    master  = new UserChallenge((long) i, users[7], challenges[i], Master, Red);
                    member1 = new UserChallenge((long) i, users[1], challenges[i], Progress, Pink);
                    member2 = new UserChallenge((long) i, users[8], challenges[i], Progress, Red);

                    userChallengeRepository.save(master);
                    userChallengeRepository.save(member1);
                    userChallengeRepository.save(member2);
                    break;
                case 6:
                    master  = new UserChallenge((long) i, users[8], challenges[i], Master, Pink);
                    member1 = new UserChallenge((long) i, users[3], challenges[i], Progress, Pink);
                    member2 = new UserChallenge((long) i, users[4], challenges[i], Progress, Red);

                    userChallengeRepository.save(master);
                    userChallengeRepository.save(member1);
                    userChallengeRepository.save(member2);
                    break;
                case 7:
                    master  = new UserChallenge((long) i, users[1], challenges[i], Master, Yellow);
                    member1 = new UserChallenge((long) i, users[2], challenges[i], Progress, Pink);
                    member2 = new UserChallenge((long) i, users[3], challenges[i], Progress, Red);

                    userChallengeRepository.save(master);
                    userChallengeRepository.save(member1);
                    userChallengeRepository.save(member2);
                    break;
                case 8:
                    master  = new UserChallenge((long) i, users[8], challenges[i], Master, Yellow);
                    member1 = new UserChallenge((long) i, users[4], challenges[i], Progress, Pink);
                    member2 = new UserChallenge((long) i, users[9], challenges[i], Progress, Red);

                    userChallengeRepository.save(master);
                    userChallengeRepository.save(member1);
                    userChallengeRepository.save(member2);
                    break;
                case 9:
                    master  = new UserChallenge((long) i, users[2], challenges[i], Master, Yellow);
                    member1 = new UserChallenge((long) i, users[6], challenges[i], Reject, Red);
                    member2 = new UserChallenge((long) i, users[7], challenges[i], Progress, Pink);

                    userChallengeRepository.save(master);
                    userChallengeRepository.save(member1);
                    userChallengeRepository.save(member2);
                    break;
            }
        }
    }


    /**
     * @description ExerciseRecord 및 Matrix 생성
     * --Matrix--
     * 약 5Km 거리의 두 지역에 공통 ExerciseRecord 및 Matrix 생성
     * 두 지역 모두 영역을 갖고 있는 사람: A, B, E, F, J
     * A지역만 갖고 있는 사람: D, H, K~O
     * B지역만 갖고 있는 사람: C, G, I, P~T
     *
     * 각 지역 별 12시 방향부터 시계방향으로 9개의 포인트 생성
     * A지역: 선부역
     * B지역: 노적봉
     *
     * --ExerciseRecord--
     * 5주 간의 운동 기록을 생성한다.
     * 이번 주 기록은 모두 월요일에 생성하고, 과거 기록은 다음과 같이 생성한다.
     * A,B 지역 모두 기록을 갖는 경우: 9개의 포인트 중 0~4는 월요일, 5~8는 금요일에 생성해, 총 2개의 운동 기록을 매 주 갖는다.
     * 한 지역의 기록만 갖는 경우: 월요일에 모든 기록을 생성한다.
     *
     * @note - 5번 챌린지(G,A,H)는 A는 둘 다 있고, D, H는 서로 다른 한 지역의 영역만 있음.
     *       - I는 D,H와 챌린지를 하지만, 겹치는 영역이 없음.
     */
    public void createExerciseRecordAndMatrix() {
        Map<String, double[]> aPlaces = new HashMap<>();
        Map<String, double[]> bPlaces = new HashMap<>();

        double[] aLatitude = {37.335474,  37.335252,  37.334832,  37.334205,  37.333410,  37.333616,  37.334373,  37.335099,  37.335428};
        double[] aLongitude = {126.809945, 126.810647, 126.811137, 126.811272, 126.810224, 126.809167, 126.808571, 126.808830, 126.809388};
        aPlaces.put("latitude", aLatitude);
        aPlaces.put("longitude", aLongitude);

        double[] bLatitude = {37.318383,  37.318117,  37.317557,  37.316585,  37.315410,  37.315621,  37.316502,  37.317355,  37.318034};
        double[] bLongitude = {126.854740, 126.855155, 126.855386, 126.855605, 126.855016, 126.854082, 126.853805, 126.853851, 126.854174};
        bPlaces.put("latitude", bLatitude);
        bPlaces.put("longitude", bLongitude);

        int[] bothPlaceUsers = {1, 2, 5, 6, 10};
        int[] aPlaceUsers = {4, 8, 11, 12, 13, 14, 15};
        int[] bPlaceUsers = {3, 7, 9, 16, 17, 18, 19, 20};


        //각 지역 과거 기록 생성
        createThisWeekRecord(aPlaces, aPlaceUsers);
        createThisWeekRecord(bPlaces, bPlaceUsers);

        //각 지역 이번 주 기록 생성
        createPastRecordsIn5Weeks(aPlaces, aPlaceUsers);
        createPastRecordsIn5Weeks(bPlaces, bPlaceUsers);

        //A, B지역 둘 다 영역이 있는 기록 생성
        createThisWeekRecord(aPlaces, bothPlaceUsers);
        createThisWeekRecord(bPlaces, bothPlaceUsers);

        //이번 주 기록 생성
        for (int userIdx : bothPlaceUsers) {
            String alias = String.valueOf((char) (64 + userIdx));

            for (int j=5; j>0; j--) { //5주간의 운동 기록 생성
                ExerciseRecord recordInMon = ExerciseRecord.builder()
                        .distance(createRandomNumber(100, 200))
                        .ended(LocalDateTime.now().minusWeeks(j).with(MONDAY))
                        .exerciseTime(createRandomNumber(1000, 3000))
                        .message(alias + "의 " + j + "주 전 운동기록(월)")
                        .started(LocalDateTime.now().minusWeeks(j).minusHours(j + 1).with(MONDAY))
                        .stepCount(createRandomNumber(1000, 1500))
                        .user(users[userIdx])
                        .build();

                for (int k=0; k<=4; k++) {
                    recordInMon.addMatrix(new Matrix(aLatitude[k], aLongitude[k]));
                    recordInMon.addMatrix(new Matrix(bLatitude[k], bLongitude[k]));
                }

                exerciseRecordRepository.save(recordInMon);

                ExerciseRecord recordInFri = ExerciseRecord.builder()
                        .distance(createRandomNumber(100, 200))
                        .ended(LocalDateTime.now().minusWeeks(j).with(FRIDAY))
                        .exerciseTime(createRandomNumber(1000, 3000))
                        .message(alias + "의 " + j + "주 전 운동기록(금)")
                        .started(LocalDateTime.now().minusWeeks(j).minusHours(j + 1).with(FRIDAY))
                        .stepCount(createRandomNumber(1000, 1500))
                        .user(users[userIdx])
                        .build();

                for (int k=5; k<=8; k++) {
                    recordInMon.addMatrix(new Matrix(aLatitude[k], aLongitude[k]));
                    recordInMon.addMatrix(new Matrix(bLatitude[k], bLongitude[k]));
                }

                exerciseRecordRepository.save(recordInFri);
            }
        }
    }

    private void createPastRecordsIn5Weeks(Map<String, double[]> places, int[] target) {
        double[] latitudes = places.get("latitude");
        double[] longitudes = places.get("longitude");

        for (int userIdx : target) {
            String alias = String.valueOf((char) (64 + userIdx));

            for (int j=5; j>0; j--) { //5주간의 운동 기록 생성
                ExerciseRecord recordInMon = ExerciseRecord.builder()
                        .distance(createRandomNumber(100, 200))
                        .ended(LocalDateTime.now().minusWeeks(j).with(MONDAY))
                        .exerciseTime(createRandomNumber(1000, 3000))
                        .message(alias + "의 " + j + "주 전 운동기록(월)")
                        .started(LocalDateTime.now().minusWeeks(j).minusHours(j + 1).with(MONDAY))
                        .stepCount(createRandomNumber(1000, 1500))
                        .user(users[userIdx])
                        .build();

                for (int k = 0; k <= 8; k++) {
                    recordInMon.addMatrix(new Matrix(latitudes[k], longitudes[k]));
                }

                exerciseRecordRepository.save(recordInMon);
            }
        }
    }

    private void createThisWeekRecord(Map<String, double[]> places, int[] target) {
        double[] latitudes = places.get("latitude");
        double[] longitudes = places.get("longitude");

        for (int userIdx : target) {
            String alias = String.valueOf((char) (64 + userIdx));

            ExerciseRecord recordInThisWeek = ExerciseRecord.builder()
                    .distance(createRandomNumber(100, 200))
                    .ended(LocalDateTime.now().with(MONDAY))
                    .exerciseTime(createRandomNumber(1000, 3000))
                    .message(alias + "의 " + "이번 주 운동 기록")
                    .started(LocalDateTime.now().minusHours(2).with(MONDAY))
                    .stepCount(createRandomNumber(1000, 1500))
                    .user(users[userIdx])
                    .build();

            for (int k = 0; k <= 8; k++) {
                recordInThisWeek.addMatrix(new Matrix(latitudes[k], longitudes[k]));
            }
            exerciseRecordRepository.save(recordInThisWeek);
        }
    }

    /**
     * @description 친구 관계 생성
     * 친구 목록
     * A B
     * A C
     * A G
     */
    private void createFriend() {
        Friend f1 = Friend.builder()
                .status(FriendStatus.Accept)
                .user(users[1])
                .friend(users[2])
                .build();

        Friend f2 = Friend.builder()
                .status(FriendStatus.Accept)
                .user(users[3])
                .friend(users[1])
                .build();

        Friend f3 = Friend.builder()
                .status(FriendStatus.Accept)
                .user(users[1])
                .friend(users[7])
                .build();

        friendRepository.save(f1);
        friendRepository.save(f2);
        friendRepository.save(f3);
    }

}