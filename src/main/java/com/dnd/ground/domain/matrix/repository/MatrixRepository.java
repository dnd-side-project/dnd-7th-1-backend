package com.dnd.ground.domain.matrix.repository;

import com.dnd.ground.domain.exerciseRecord.ExerciseRecord;
import com.dnd.ground.domain.matrix.Matrix;
import com.dnd.ground.domain.matrix.dto.MatrixDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @description 운동 영역 리포지토리 인터페이스
 * @author  박세헌
 * @since   2022-08-01
 * @updated 1.운동 기록의 매트릭스 리스트 조회 쿼리 추가
 *          -2022.09.29 박찬호
 */

public interface MatrixRepository extends JpaRepository<Matrix, Long>, MatrixRepositoryQuery {

    // 운동기록들을 통해 매트릭스 정보 조회(중복o)
    @Query("select new com.dnd.ground.domain.matrix.dto.MatrixDto(m.latitude, m.longitude) " +
            "from Matrix m join m.exerciseRecord e where e in :exerciseRecords")
    List<MatrixDto> findMatrixByRecords(List<ExerciseRecord> exerciseRecords);

    // 운동기록들을 통해 매트릭스 정보 조회(중복x)
    @Query("select distinct new com.dnd.ground.domain.matrix.dto.MatrixDto(m.latitude, m.longitude) " +
            "from Matrix m join m.exerciseRecord e where e in :exerciseRecords")
    List<MatrixDto> findMatrixSetByRecords(List<ExerciseRecord> exerciseRecords);


    // 운동기록을 통해 매트릭스 정보 조회(중복x)
    @Query("select distinct new com.dnd.ground.domain.matrix.dto.MatrixDto(m.latitude, m.longitude) " +
            "from Matrix m join m.exerciseRecord e where e = :exerciseRecord")
    List<MatrixDto> findMatrixSetByRecord(ExerciseRecord exerciseRecord);

    // 운동 기록의 매트릭스 리스트 조회
    @Query("select m from Matrix m where m.exerciseRecord=:exerciseRecord")
    List<Matrix> findByRecord(@Param("exerciseRecord") ExerciseRecord exerciseRecord);

}
