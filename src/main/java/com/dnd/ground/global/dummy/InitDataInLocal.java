package com.dnd.ground.global.dummy;

import com.dnd.ground.domain.challenge.*;
import com.dnd.ground.domain.challenge.repository.ChallengeRepository;
import com.dnd.ground.domain.challenge.repository.UserChallengeRepository;
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

import static com.dnd.ground.domain.challenge.ChallengeColor.*;
import static com.dnd.ground.domain.challenge.ChallengeStatus.*;
import static com.dnd.ground.domain.challenge.ChallengeType.Accumulate;
import static com.dnd.ground.domain.challenge.ChallengeType.Widen;
import static java.time.DayOfWeek.*;

/**
 * @description Test data 생성
 * @author  박찬호
 * @since   2023-02-07
 * @updated 1. User, Challenge, UserChallenge 데이터 생성
 *          - 2023.02.07 박찬호
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

    private final int CHALLENGE_COUNT = 9;
    private final int USER_COUNT = 20;
    private final User[] users = new User[USER_COUNT+1];
    private final Challenge[] challenges = new Challenge[CHALLENGE_COUNT+1];

    @PostConstruct
    public void init() {
        createUser();
        createChallenge();
        createUserChallengeRelation();
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
                    .created(LocalDateTime.now().minusDays(i))
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
            StringBuilder sb = new StringBuilder();
            sb.append(String.valueOf(i).repeat(32));

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
                    .uuid(sb.toString())
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
}
