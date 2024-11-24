package com.ico.api.controller;


import com.ico.api.service.stock.InvestService;
import com.ico.api.service.stock.StockService;
import com.ico.core.dto.InvestReqDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * @author 변윤경
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/invest")
public class InvestController {

    private final InvestService investService;

    /**
     * 주식 매수
     *
     * @param dto 현재 지수, 매수 금액
     * @return Httpstatus
     */
    @PostMapping("/student")
    public ResponseEntity<Void> buyStock(HttpServletRequest request, @Valid @RequestBody InvestReqDto dto) {
        investService.buyStock(request, dto.getStockItemId(), dto.getPrice(), dto.getAmount());
        return ResponseEntity.ok().build();
    }

    /**
     * 주식 매도
     *
     * @return Httpstatus
     */
    @DeleteMapping("/student/{stockItemId}")
    public ResponseEntity<Void> sellStock(HttpServletRequest request, @PathVariable Long stockItemId) {
        investService.sellStock(request, stockItemId);
        return ResponseEntity.ok().build();
    }


}

