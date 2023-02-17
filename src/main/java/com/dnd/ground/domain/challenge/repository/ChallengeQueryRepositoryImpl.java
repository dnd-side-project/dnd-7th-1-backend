package com.dnd.ground.domain.challenge.repository;

import com.dnd.ground.domain.challenge.Challenge;
import com.dnd.ground.domain.challenge.ChallengeStatus;
import com.dnd.ground.domain.challenge.QUserChallenge;
import com.dnd.ground.domain.challenge.dto.ChallengeColorDto;
import com.dnd.ground.domain.challenge.dto.UCDto;
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
 * @author 박찬호
 * @description QueryDSL을 활용한 챌린지 관련 구현체
 * @updated 1.메인화면 조회 시 필요한 쿼리 생성
 * - 2023.02.15 박찬호
 * @since 2023-02-15
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
                        userChallenge.challenge.eq(challenge),
                        challenge.status.eq(ChallengeStatus.PROGRESS),
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
                        userChallenge.challenge.eq(challenge),
                        challenge.status.eq(ChallengeStatus.PROGRESS),
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
                        userChallenge.challenge.eq(challenge),
                        userChallenge.status.eq(ChallengeStatus.PROGRESS),
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

    /*진행 중인 챌린지 정보 조회(색깔)*/
    @Override
    public List<ChallengeColorDto> findProgressChallengesColor(User user) {
        return queryFactory
                .select(Projections.constructor(ChallengeColorDto.class,
                                userChallenge.challenge,
                                userChallenge.color
                        )
                )
                .from(userChallenge)
                .innerJoin(challenge)
                .on(
                        userChallenge.challenge.eq(challenge),
                        challenge.status.eq(ChallengeStatus.PROGRESS)
                )
                .where(userChallenge.user.eq(user))
                .fetch();
    }


    /*진행 중인 챌린지 개수 조회*/
    @Override
    public Map<User, Long> findUsersProgressChallengeCount(Set<String> users) {
        return queryFactory
                .from(userChallenge)
                .where(
                        userChallenge.user.nickname.in(users),
                        userChallenge.status.in(ChallengeStatus.PROGRESS, ChallengeStatus.MASTER, ChallengeStatus.PROGRESS.WAIT)
                )
                .groupBy(userChallenge.user)
                .transform(groupBy(userChallenge.user).as(userChallenge.count()));
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
}