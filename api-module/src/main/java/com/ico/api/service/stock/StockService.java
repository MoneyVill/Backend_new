package com.ico.api.service.stock;

import com.ico.api.dto.stock.StockItemResDto;
import com.ico.api.dto.stock.StockStudentResDto;
import com.ico.api.dto.stock.StockTeacherResDto;
import com.ico.api.dto.stock.StockUploadReqDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author 변윤경
 */
public interface StockService {

    /**
     * 교사 투자 이슈 목록 조회
     *
     * @return 교사 화면의 투자 이슈 정보
     */
    StockTeacherResDto getIssueTeacher(HttpServletRequest request, Long stockItemId);

    /**
     * 학생 투자 이슈 목록 조회
     * @return 학생 화면의 투자 이슈 정보
     */
    StockStudentResDto getIssueStudent(HttpServletRequest request, Long stockItemId);

    /**
     * 투자 이슈 등록
     * @param dto 변동률, 이슈, 오늘의 가격
     */
    void uploadIssue(HttpServletRequest request, StockUploadReqDto dto);

    /**
     * 투자 종목 삭제
     *
     * @param request
     */
    void deleteStock(HttpServletRequest request, Long stockItemId);

    /**
     * 특정 나라의 투자 종목 조회
     *
     * @param request
     * @return 투자 종목 목록
     */
    List<StockItemResDto> getStockItems(HttpServletRequest request);

    /**
     * 특정 나라의 모든 투자 종목 조회 (선생님과 학생 모두 조회 가능)
     *
     * @param request
     * @return 투자 종목 목록
     */
    List<StockItemResDto> getAllStocks(HttpServletRequest request);
}
