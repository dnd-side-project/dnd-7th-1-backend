package com.dnd.ground.global.batch.challenge;

import com.dnd.ground.domain.challenge.Challenge;
import com.dnd.ground.domain.challenge.UserChallenge;
import com.dnd.ground.global.log.CommonLogger;
import org.springframework.batch.item.database.AbstractPagingItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessResourceFailureException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @description 챌린지-UC 간 조회를 위한 ItemReader.
 *              챌린지를 기준으로 chunk를 나누되 해당 챌린지의 UC가 모두 필요한 상황이므로, ItemReader를 상속 받아 커스텀한 구현체.
 *              챌린지 시작, 종료 등 챌린지 관련 배치 작업에서 사용해야 하므로 싱글톤인 빈으로 관리하지 않고 객체를 생성해서 사용하는 방식으로 활용.
 *              JpaPagingItemReader.class, AbstractPagingItemReader.class의 동작 방식을 참고해 트랜잭션 관리 및 페이징 적용.
 *              결과적으로, 챌린지를 기준으로 페이징이 적용된다.
 * @author  박찬호
 * @since   2023-04-14
 * @updated 1. 조회 시, 비관적 락(배타 락)을 걸도록 수정
 *          2. UC 조회 쿼리 수정
 *          - 2023-04-26 박찬호
 */
public class ChallengeWithUCItemReader extends AbstractPagingItemReader<ChallengeWithUCDto> {

    private EntityManager em;
    private final EntityManagerFactory emf;

    private int page;
    private int pageSize;
    private final Map<String, Object> challengeParamMap;
    private final CommonLogger logger;

    public ChallengeWithUCItemReader(Map<String, Object> challengeParamMap,
                                     EntityManagerFactory entityManagerFactory,
                                     @Qualifier("batchLogger") CommonLogger logger) {
        this.challengeParamMap = challengeParamMap;
        this.emf = entityManagerFactory;
        this.page = getPage();
        this.pageSize = getPageSize();
        this.logger = logger;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
        super.setPageSize(pageSize);
    }

    public int getPage() {
        return super.getPage();
    }

    @Override
    protected void doOpen() throws Exception {
        super.doOpen();

        em = emf.createEntityManager();
        if (em == null) {
            throw new DataAccessResourceFailureException("챌린지 시작 배치: EntityManager를 생성할 수 없습니다.");
        }
    }

    /**
     * AbstractPagingItemReader.class에서 페이지를 읽기 위한 메소드 Override.
     * 결과 배열 및 트랜잭션 관리를 한다.
     */
    @Override
    protected void doReadPage() {
        System.out.println("---- READER START ----");
        //결과 배열 및 트랜잭션, em 초기화
        if (results == null) results = new CopyOnWriteArrayList<>();
        else results.clear();

        em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        em.flush();
        em.clear();

        try {
            results.addAll(executeQuery());
            tx.commit();
        } catch (Exception e) {
            logger.errorWrite(String.format("챌린지 시작 배치: 쿼리 중 예외가 발생했습니다. MSG: %s", e.getMessage()));
            tx.rollback();
        }
    }

    /**
     * 실제 쿼리를 날려서 받는 역할을 한다.
     */
    public List<ChallengeWithUCDto> executeQuery() {
        List<ChallengeWithUCDto> response = new ArrayList<>();

        TypedQuery<Challenge> challengeTypedQuery =
                em.createQuery(
                        "SELECT c " +
                                "FROM Challenge c " +
                                "WHERE c.started <= :jobParam AND c.status = :status",
                        Challenge.class);

        if (challengeParamMap != null) {
            for (Map.Entry<String, Object> param : challengeParamMap.entrySet()) {
                challengeTypedQuery.setParameter(param.getKey(), param.getValue());
            }
        }


        List<Challenge> challenges = challengeTypedQuery
                .setFirstResult(page * pageSize)
                .setMaxResults(pageSize)
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .getResultList();

        for (Challenge challenge : challenges) {
            List<UserChallenge> ucs = em.createQuery(
                            "SELECT uc " +
                                    "FROM UserChallenge uc " +
                                    "INNER JOIN FETCH Challenge c " +
                                    "ON uc.challenge = c " +
                                    "INNER JOIN FETCH User u " +
                                    "ON uc.user = u " +
                                    "INNER JOIN FETCH UserProperty up " +
                                    "ON u.property = up " +
                                    "WHERE uc.challenge = :challenge", UserChallenge.class)
                    .setParameter("challenge", challenge)
                    .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                    .getResultList();

            response.add(new ChallengeWithUCDto(challenge, ucs));
        }

        return response;
    }

    @Override
    protected void doJumpToPage(int itemIndex) {
    }
}