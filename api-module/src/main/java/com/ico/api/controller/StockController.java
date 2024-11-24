package com.ico.api.controller;

import com.ico.api.dto.stock.StockItemResDto;
import com.ico.api.dto.stock.StockStudentResDto;
import com.ico.api.dto.stock.StockTeacherResDto;
import com.ico.api.dto.stock.StockUploadReqDto;
import com.ico.api.service.stock.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 *
 * @author 변윤경
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stock")
public class StockController {
    private final StockService stockService;

    /**
     * 교사의 투자 이슈 조회
     * @return 투자 이슈 정보, 투자 종목 정보(거래가능시간, 이름)
     */
    @GetMapping("/teacher/{stockItemId}")
    public ResponseEntity<StockTeacherResDto> stockIssueTeacher(HttpServletRequest request, @PathVariable Long stockItemId) {
        return ResponseEntity.ok(stockService.getIssueTeacher(request, stockItemId));
    }

    /**
     * 투자 이슈 등록
     * @param dto 지수, 내일의 이슈
     * @return Httpstatus
     */
    @PostMapping("/teacher/upload")
    public ResponseEntity<Void> uploadIssue(HttpServletRequest request, @Valid @RequestBody StockUploadReqDto dto) {
        stockService.uploadIssue(request, dto);
        return ResponseEntity.ok().build();
    }

    /**
     * 학생의 투자이슈 조회
     * @return 투자 이슈
     */
    @GetMapping("/student/{stockItemId}")
    public ResponseEntity<StockStudentResDto> stockIssueStudent(HttpServletRequest request, @PathVariable Long stockItemId) {
        return ResponseEntity.ok(stockService.getIssueStudent(request, stockItemId));
    }

    /**
     * 투자 종목 삭제
     *
     * @param request
     * @return
     */
    @DeleteMapping("/teacher/{stockItemId}")
    public ResponseEntity<Void> deleteStock(HttpServletRequest request, @PathVariable Long stockItemId) {
        stockService.deleteStock(request, stockItemId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/student/items")
    public ResponseEntity<List<StockItemResDto>> getStockItems(HttpServletRequest request) {
        return ResponseEntity.ok(stockService.getStockItems(request));
    }

    /**
     * 투자 종목 목록 조회
     *
     * @param request
     * @return 투자 종목 목록
     */
    @GetMapping("/items")
    public ResponseEntity<List<StockItemResDto>> getAllStocks(HttpServletRequest request) {
        return ResponseEntity.ok(stockService.getAllStocks(request));
    }
}
