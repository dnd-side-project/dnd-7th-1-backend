package com.dnd.ground.domain.challenge.service;

import com.dnd.ground.domain.challenge.Challenge;
import com.dnd.ground.domain.challenge.ChallengeStatus;
import com.dnd.ground.domain.challenge.UserChallenge;
import com.dnd.ground.domain.challenge.dto.ChallengeCreateRequestDto;
import com.dnd.ground.domain.challenge.dto.ChallengeRequestDto;
import com.dnd.ground.domain.challenge.dto.ChallengeResponseDto;
import com.dnd.ground.domain.challenge.repository.ChallengeRepository;
import com.dnd.ground.domain.challenge.repository.UserChallengeRepository;
import com.dnd.ground.domain.matrix.matrixService.MatrixService;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.dto.RankResponseDto;
import com.dnd.ground.domain.user.dto.UserResponseDto;
import com.dnd.ground.domain.user.repository.UserRepository;
import com.dnd.ground.global.util.UuidUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @description 챌린지와 관련된 서비스의 역할을 분리한 구현체
 * @author  박찬호
 * @since   2022-08-03
 * @updated 1. 진행 중 상태의 챌린지 조회 기능 구현
 *          2. 완료된 챌린지 조회 기능 구현
 *          - 2022.08.15 박찬호
 */

@Slf4j
@RequiredArgsConstructor
@Service
public class ChallengeServiceImpl implements ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final UserChallengeRepository userChallengeRepository;
    private final UserRepository userRepository;
    private final MatrixService matrixService;

    /*챌린지 생성*/
    @Transactional
    public ResponseEntity<?> createChallenge(ChallengeCreateRequestDto requestDto) {

        User master = userRepository.findByNickname(requestDto.getNickname()).orElseThrow(); //예외 처리 예정

        Challenge challenge = Challenge.create()
                .uuid(UuidUtil.createUUID())
                .name(requestDto.getName())
                .started(requestDto.getStarted())
                .message(requestDto.getMessage()) //메시지 처리 방식 결과에 따라 수정 요망
                .color(requestDto.getColor())
                .type(requestDto.getType())
                .build();

        challengeRepository.save(challenge);

        for (String nickname : requestDto.getFriends()) {
            User user = userRepository.findByNickname(nickname).orElseThrow(); //예외 처리 예정
            userChallengeRepository.save(new UserChallenge(challenge, user));
        }

        UserChallenge masterChallenge = userChallengeRepository.save(new UserChallenge(challenge, master));
        masterChallenge.changeStatus(ChallengeStatus.Master);

        return new ResponseEntity(HttpStatus.CREATED);
    }

    /*유저-챌린지 상태 변경*/
    @Transactional
    public ResponseEntity<?> changeUserChallengeStatus(ChallengeRequestDto.CInfo requestDto, ChallengeStatus status) {
        //정보 조회
        Challenge challenge = challengeRepository.findByUuid(requestDto.getUuid());
        User user = userRepository.findByNickname(requestDto.getNickname()).orElseThrow(); // 예외 처리 예정
        //상태 변경
        UserChallenge userChallenge = userChallengeRepository.findByUserAndChallenge(user, challenge).orElseThrow();
        if (userChallenge.getStatus() == ChallengeStatus.Master) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        userChallenge.changeStatus(status);

        return new ResponseEntity(HttpStatus.OK);
    }

    /*챌린지 상태 변경(매일 00:00 실행)*/
    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void startPeriodChallenge() {
        //챌린지 시작일이 오늘인 챌린지 리스트
        List<Challenge> challenges = challengeRepository.findChallengesNotStarted(LocalDate.now());
        int countDelete = 0; //삭제된 챌린지 수
        int countUser = 0; //삭제된 유저 수
        int countProgress = 0; // 진행 상태로 바뀐 챌린지 수

        for (Challenge challenge : challenges) {
            //Wait, Reject 상태의 유저 삭제
            countUser += userChallengeRepository.deleteUCByChallenge(challenge);

            List<UserChallenge> userChallenge = userChallengeRepository.findUCByChallenge(challenge);

            //주최자만 남은 경우 챌린지와 주최자 삭제
            if (userChallenge.size() == 1) {
                userChallengeRepository.delete(userChallenge.get(0));
                challengeRepository.delete(challenge);
                countDelete++;
            }
            //챌린지 진행 상태로 변경
            else {
                challenge.updateStatus(ChallengeStatus.Progress);
            }
        }

        log.info("**챌린지 시작 메소드 실행** 현재 시간:{} | 삭제된 챌린지 개수:{} | 삭제된 유저 수:{} | 진행 상태로 바뀐 챌린지 개수:{}",
                LocalDateTime.now(), countDelete, countUser, countProgress);
    }

    /*일주일 챌린지 마감(매주 일요일 오후 11시 59분 50초 실행)*/
    @Transactional
    @Scheduled(cron = "50 59 23 * * 0")
    public void endPeriodChallenge() {
        //진행 중인 챌린지 모두 조회
        List<Challenge> challenges = challengeRepository.findChallengesByStatusEquals(ChallengeStatus.Progress);

        for (Challenge challenge : challenges) {
            //챌린지 완료
            challenge.updateStatus(ChallengeStatus.Done);

            //각 유저들도 완료 상태 변경
            List<UserChallenge> userChallenge = userChallengeRepository.findUCByChallenge(challenge);
            userChallenge.forEach(uc -> uc.changeStatus(ChallengeStatus.Done));
        }

        log.info("**챌린지 종료 메소드 실행** 현재 시간:{} | 종료된 챌린지 개수:{}", LocalDateTime.now(), challenges.size());
    }
    
    /*초대 받은 챌린지 조회*/
    public List<ChallengeResponseDto.Invite> findInviteChallenge(String nickname) {
        User user = userRepository.findByNickname(nickname).orElseThrow(); //예외 처리 예정

        List<Challenge> challenges = challengeRepository.findChallengeInWait(user);
        List<ChallengeResponseDto.Invite> response = new ArrayList<>();

        for (Challenge challenge : challenges) {

            response.add(
                    ChallengeResponseDto.Invite.builder()
                            .name(challenge.getName())
                            .InviterNickname(userChallengeRepository.findMasterInChallenge(challenge).getNickname())
                            .message(challenge.getMessage())
                            .created(challenge.getCreated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                            .build()
            );
        }

        return response;
    }

    /*진행 대기 중인 챌린지 리스트 조회*/
    public List<ChallengeResponseDto.Wait> findWaitChallenge(String nickname) {
        User user = userRepository.findByNickname(nickname).orElseThrow(); //예외 처리 예정

        List<Challenge> waitChallenge = challengeRepository.findWaitChallenge(user);
        List<ChallengeResponseDto.Wait> response = new ArrayList<>();

        for (Challenge challenge : waitChallenge) {

            LocalDate started = challenge.getStarted();

            response.add(
                    ChallengeResponseDto.Wait.builder()
                            .name(challenge.getName())
                            .started(started)
                            .ended(started.plusDays(7-started.getDayOfWeek().getValue()))
                            .totalCount(userChallengeRepository.findUCCount(challenge)) //챌린지에 참여하는 전체 인원 수
                            .readyCount(userChallengeRepository.findUCWaitCount(challenge) + 1) //Progress 상태 회원 수 + 주최자
                            .build()
            );
        }

        return response;
    }

    /*진행 중인 챌린지 리스트 조회*/
    public List<ChallengeResponseDto.Progress> findProgressChallenge(String nickname) {
        User user = userRepository.findByNickname(nickname).orElseThrow(); //예외 처리 예정

        List<Challenge> progressChallenge = challengeRepository.findProgressChallenge(user);
        List<ChallengeResponseDto.Progress> response = new ArrayList<>();

        for (Challenge challenge : progressChallenge) {
            Integer rank = -1; //랭킹
            LocalDate started = challenge.getStarted(); //챌린지 시작 날짜

            //해당 회원의 랭킹 추출
            RankResponseDto.Area rankList = matrixService.challengeRank(challenge, started.atStartOfDay(), LocalDateTime.now());

            for (UserResponseDto.Ranking ranking : rankList.getAreaRankings()) {
                if (ranking.getNickname().equals(nickname)) {
                    rank = ranking.getRank();
                    break;
                }
            }

            response.add(
                    ChallengeResponseDto.Progress.builder()
                            .name(challenge.getName())
                            .started(started)
                            .ended(started.plusDays(7-started.getDayOfWeek().getValue()))
                            .rank(rank) //!!랭킹 == -1에 대한 예외 처리 필요
                            .build()
            );
        }

        return response;
    }

    /*진행 완료된 챌린지 리스트 조회*/
    public List<ChallengeResponseDto.Done> findDoneChallenge(String nickname) {
        User user = userRepository.findByNickname(nickname).orElseThrow(); //예외 처리 예정

        List<Challenge> doneChallenge = challengeRepository.findDoneChallenge(user);
        List<ChallengeResponseDto.Done> response = new ArrayList<>();

        for (Challenge challenge : doneChallenge) {
            Integer rank = -1; //랭킹
            LocalDate started = challenge.getStarted(); //챌린지 시작 날짜

            //해당 회원의 랭킹 추출
            RankResponseDto.Area rankList = matrixService.challengeRank(challenge, started.atStartOfDay(), LocalDateTime.now());

            for (UserResponseDto.Ranking ranking : rankList.getAreaRankings()) {
                if (ranking.getNickname().equals(nickname)) {
                    rank = ranking.getRank();
                    break;
                }
            }

            response.add(
                    ChallengeResponseDto.Done.builder()
                            .name(challenge.getName())
                            .started(started)
                            .ended(started.plusDays(7-started.getDayOfWeek().getValue()))
                            .rank(rank) //!!랭킹 == -1에 대한 예외 처리 필요
                            .build()
            );
        }

        return response;
    }

}
