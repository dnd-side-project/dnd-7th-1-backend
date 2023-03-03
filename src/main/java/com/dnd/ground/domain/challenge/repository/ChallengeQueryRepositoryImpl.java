package com.dnd.ground.domain.challenge.repository;

import com.dnd.ground.domain.challenge.*;
import com.dnd.ground.domain.challenge.dto.ChallengeColorDto;
import com.dnd.ground.domain.challenge.dto.ChallengeCond;
import com.dnd.ground.domain.challenge.dto.QUCDto_UCInfo;
import com.dnd.ground.domain.challenge.dto.UCDto;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.global.util.UuidUtil;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
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
 * @updated 1.상태에 따른 챌린지 조회 쿼리를 동적 쿼리를 활용해 챌린지 조회 쿼리로 수정
 *          2.챌린지 색상 조회 쿼리의 파라미터 수정
 *          - 2023.03.03 박찬호
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
    public Map<Challenge, ChallengeColor> findChallengesColor(ChallengeCond condition) {
        return queryFactory
                .select(Projections.constructor(ChallengeColorDto.class,
                                userChallenge.challenge,
                                userChallenge.color
                        )
                )
                .from(userChallenge)
                .innerJoin(challenge)
                .on(eqChallengeAndStatus(condition.getStatus()))
                .where(userChallenge.user.eq(condition.getUser()))
                .transform(
                        groupBy(challenge).as(userChallenge.color)
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

    /*조건에 따른 조회*/
    @Override
    public List<Challenge> findChallengesByCond(ChallengeCond condition) {
        return queryFactory
                .selectFrom(challenge)
                .innerJoin(userChallenge)
                .on(
                        eqChallengeAndStatus(condition.getStatus())
                )
                .where(
                        userChallenge.user.eq(condition.getUser()),
                        inPeriod(condition.getStarted(), condition.getEnded())
                )
                .orderBy(challenge.created.asc())
                .fetch();
    }

    private Predicate inPeriod(LocalDateTime started, LocalDateTime ended) {
        return started != null && ended != null ? challenge.started.goe(started).and(challenge.ended.loe(ended)) : null;
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
    public Map<Challenge, List<UCDto.UCInfo>> findUCInChallenge(ChallengeCond condition) {
        QUserChallenge subUC = new QUserChallenge("subUC");

        return queryFactory
                .from(challenge)
                .innerJoin(userChallenge)
                .on(
                        eqChallengeAndStatus(condition.getStatus()),
                        userChallenge.challenge.in(
                                JPAExpressions
                                        .select(subUC.challenge)
                                        .from(subUC)
                                        .where(subUC.user.eq(condition.getUser()))
                        )
                )
                .innerJoin(user)
                .on(user.eq(userChallenge.user))
                .where(
                        ucEqChallengeUuid(condition.getUuid())
                )
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
        return status != null ? userChallenge.challenge.eq(challenge).and(challenge.status.eq(status)) : userChallenge.challenge.eq(challenge);
    }

    private BooleanExpression ucEqChallengeUuid(byte[] uuid) {
        return uuid != null ? userChallenge.challenge.uuid.eq(uuid) : null;
    }
}