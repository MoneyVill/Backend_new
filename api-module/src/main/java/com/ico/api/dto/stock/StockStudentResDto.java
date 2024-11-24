package com.ico.api.dto.stock;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

/**
 * @author 변윤경
 */
@Getter
@Setter
@NoArgsConstructor
public class StockStudentResDto {
    private Long stockItemId;
    private String stockName;
    private int account;
    private String stock;
    private LocalTime tradingStart;
    private LocalTime tradingEnd;
    private StockMyResDto myStock;
    private List<StockColDto> issue;

    @Builder
    public StockStudentResDto(Long stockItemId, String stockName, int account, String stock, LocalTime tradingStart,
                              LocalTime tradingEnd, StockMyResDto myStock, List<StockColDto> issue) {
        this.stockItemId = stockItemId;
        this.stockName = stockName;
        this.account = account;
        this.stock = stock;
        this.tradingStart = tradingStart;
        this.tradingEnd = tradingEnd;
        this.myStock = myStock;
        this.issue = issue;
    }
}

