package com.dnd.ground.domain.challenge.repository;

import com.dnd.ground.domain.challenge.*;
import com.dnd.ground.domain.challenge.dto.ChallengeColorDto;
import com.dnd.ground.domain.challenge.dto.QUCDto_UCInfo;
import com.dnd.ground.domain.challenge.dto.UCDto;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.global.util.UuidUtil;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.dnd.ground.domain.challenge.QChallenge.challenge;
import static com.dnd.ground.domain.challenge.QUserChallenge.userChallenge;
import static com.dnd.ground.domain.user.QUser.user;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;

/**
 * @description QueryDSL을 활용한 챌린지 관련 구현체
 * @author 박찬호
 * @since 2023-02-15
 * @updated 1.회원 닉네임, 챌린지 UUID를 통해 UC 조회하는 쿼리 생성
 *          2.챌린지 상태에 따라, 챌린지와 챌린지에 참여하고 있는 인원의 UC 조회
 *          - 2023.02.28 박찬호
 */
@RequiredArgsConstructor
@Slf4j
public class ChallengeQueryRepositoryImpl implements ChallengeQueryRepository {
    private final JPAQueryFactory queryFactory;


    /*진행 중인 챌린지 멤버 조회*/
    @Override
    public List<User> findUCInProgress(User user) {
        return queryFactory
                .select(userChallenge.user)
                .from(userChallenge)
                .innerJoin(challenge)
                .on(
                        eqChallengeAndStatus(ChallengeStatus.PROGRESS),
                        containUserInChallenge(user)
                )
                .where(userChallenge.user.ne(user))
                .distinct()
                .fetch();
    }

    /*진행 중인 챌린지 개수 조회*/
    @Override
    public Map<User, Long> findUsersProgressChallengeCount(User user) {
        return queryFactory
                .select(userChallenge.count(),
                        userChallenge.user)
                .from(userChallenge)
                .innerJoin(challenge)
                .on(
                        eqChallengeAndStatus(ChallengeStatus.PROGRESS),
                        containUserInChallenge(user)
                )
                .where(userChallenge.user.ne(user))
                .groupBy(userChallenge.user)
                .transform(groupBy(userChallenge.user).as(userChallenge.count()));
    }

    /*진행 중인 챌린지 정보 조회(인원)*/
    @Override
    public Map<User, Challenge> findProgressChallengesInfo(User user) {
        return queryFactory
                .select(userChallenge.user, userChallenge.challenge)
                .from(userChallenge)
                .innerJoin(challenge)
                .on(
                        eqChallengeAndStatus(ChallengeStatus.PROGRESS),
                        containUserInChallenge(user)
                )
                .where(userChallenge.user.ne(user))
                .orderBy(challenge.started.asc())
                .transform(groupBy(userChallenge.user).as(userChallenge.challenge));
    }

    /*챌린지 상태에 따른 색깔 조회*/
    @Override
    public Map<Challenge, ChallengeColor> findChallengesColor(User user, ChallengeStatus status) {
        return queryFactory
                .select(Projections.constructor(ChallengeColorDto.class,
                                userChallenge.challenge,
                                userChallenge.color
                        )
                )
                .from(userChallenge)
                .innerJoin(challenge)
                .on(
                        eqChallengeAndStatus(status)
                )
                .where(userChallenge.user.eq(user))
                .transform(
                        groupBy(challenge)
                                .as(userChallenge.color)
                );
    }


    /*진행 중인 챌린지 개수 조회*/
    @Override
    public Map<User, Long> findUsersProgressChallengeCount(Set<String> users) {
        return queryFactory
                .from(userChallenge)
                .where(
                        userChallenge.user.nickname.in(users),
                        userChallenge.status.in(ChallengeStatus.PROGRESS, ChallengeStatus.MASTER, ChallengeStatus.WAIT)
                )
                .groupBy(userChallenge.user)
                .transform(groupBy(userChallenge.user).as(userChallenge.count()));
    }

    /*초대받은 챌린지 조회*/
    @Override
    public List<Challenge> findChallengesByUserInStatus(User user, ChallengeStatus status) {
        return queryFactory
                .selectFrom(challenge)
                .innerJoin(userChallenge)
                .on(
                        eqChallengeAndStatus(status),
                        userChallenge.user.eq(user)
                )
                .orderBy(challenge.created.asc())
                .fetch();
    }

    /*닉네임과 UUID를 기반으로 UC조회*/
    @Override
    public UserChallenge findUC(String nickname, String uuid) {
        return queryFactory
                .selectFrom(userChallenge)
                .innerJoin(challenge)
                .on(
                        userChallenge.challenge.eq(challenge),
                        challenge.uuid.eq(UuidUtil.hexToBytes(uuid))
                )
                .innerJoin(user)
                .on(
                        userChallenge.user.eq(user),
                        user.nickname.eq(nickname)
                )
                .fetchOne();
    }

    /*챌린지 상태에 따라, 챌린지와 챌린지에 참여하고 있는 인원의 UC 조회*/
    @Override
    public Map<Challenge, List<UCDto.UCInfo>> findUCPerChallenges(User targetUser, ChallengeStatus status) {
        QUserChallenge subUC = new QUserChallenge("subUC");

        return queryFactory
                .from(challenge)
                .innerJoin(userChallenge)
                .on(
                        eqChallengeAndStatus(status),
                        userChallenge.challenge.in(
                                JPAExpressions
                                        .select(subUC.challenge)
                                        .from(subUC)
                                        .where(subUC.user.eq(targetUser))
                        )
                )
                .innerJoin(user)
                .on(user.eq(userChallenge.user))
                .orderBy(challenge.created.asc())
                .transform(
                        groupBy(challenge).as(
                                list(new QUCDto_UCInfo(userChallenge.user.picturePath, userChallenge.user.nickname, userChallenge.status))
                        )
                );
    }

    private BooleanExpression containUserInChallenge(User user) {
        QUserChallenge ucSub = new QUserChallenge("ucSub");

        return user != null ? challenge.id.in(
                JPAExpressions
                        .select(ucSub.challenge.id)
                        .from(ucSub)
                        .where(ucSub.user.eq(user)))
                :
                null;
    }

    private BooleanExpression eqChallengeAndStatus(ChallengeStatus status) {
        return userChallenge.challenge.eq(challenge)
                .and(challenge.status.eq(status));
    }
}