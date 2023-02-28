package com.dnd.ground.domain.challenge.repository;

import com.dnd.ground.domain.challenge.*;
import com.dnd.ground.domain.challenge.dto.ChallengeColorDto;
import com.dnd.ground.domain.user.User;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.dnd.ground.domain.challenge.QChallenge.challenge;
import static com.dnd.ground.domain.challenge.QUserChallenge.userChallenge;
import static com.querydsl.core.group.GroupBy.groupBy;

/**
 * @description QueryDSL을 활용한 챌린지 관련 구현체
 * @author 박찬호
 * @since 2023-02-15
 * @updated 1.챌린지, UC 및 상태 조건식 메소드로 추출
 *          2.챌린지 색깔 조회 쿼리 결과 Map으로 변경
 *          3.회원이 참여하고 있는 챌린지 조회 쿼리 생성
 * - 2023.02.15 박찬호
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
        List<Tuple> queryResult = queryFactory
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
                .fetch();

        Map<User, Long> result = new HashMap<>();
        for (Tuple tuple : queryResult) {
            result.put(tuple.get(userChallenge.user), tuple.get(userChallenge.count()));
        }
        return result;
    }

    /*진행 중인 챌린지 정보 조회(인원)*/
    @Override
    public Map<User, Challenge> findProgressChallengesInfo(User user) {
        List<Tuple> queryResult = queryFactory
                .select(userChallenge.user, userChallenge.challenge)
                .from(userChallenge)
                .innerJoin(challenge)
                .on(
                        eqChallengeAndStatus(ChallengeStatus.PROGRESS),
                        containUserInChallenge(user)
                )
                .where(userChallenge.user.ne(user))
                .orderBy(challenge.started.asc())
                .fetch();

        Map<User, Challenge> result = new HashMap<>();
        for (Tuple tuple : queryResult) {
            result.put(tuple.get(userChallenge.user), tuple.get(userChallenge.challenge));
        }
        return result;
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
                .transform(groupBy(challenge).as(userChallenge.color));
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