package com.ico.core.repository;

import com.ico.core.entity.Invest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvestRepository extends JpaRepository<Invest, Long> {

    /**
     * 학생의 주식 매수 여부 확인
     * @param studentId 학생 IDX
     * @return 매수 내역
     */
    Optional<Invest> findByStudentId(Long studentId);

    /**
     * 학생 ID와 주식 항목 ID로 조회
     * @param studentId 학생 IDX
     * @param stockItemId 주식 항목 IDX
     * @return 매수 내역
     */
    Optional<Invest> findByStudentIdAndStockItemId(Long studentId, Long stockItemId);

    /**
     * 특정 주식 항목에 대한 모든 투자 내역 조회
     * @param stockItemId 주식 항목 IDX
     * @return 투자 내역 리스트
     */
    List<Invest> findAllByStockItemId(Long stockItemId);

    /**
     * 특정 국가에 속한 모든 학생들의 투자 내역 조회
     * @param nationId 국가 IDX
     * @return 투자 내역 리스트
     */
    List<Invest> findAllByStudent_Nation_Id(Long nationId);

    /**
     * 특정 국가에 속한 모든 주식 항목에 대한 투자 내역 조회
     * @param nationId 국가 IDX
     * @return 투자 내역 리스트
     */
    List<Invest> findAllByStockItem_Nation_Id(Long nationId);
}
