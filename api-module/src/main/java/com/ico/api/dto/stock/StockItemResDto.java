package com.ico.api.dto.stock;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
public class StockItemResDto {
    private Long id;
    private String name;
    private LocalTime tradingStart;
    private LocalTime tradingEnd;

    @Builder
    public StockItemResDto(Long id, String name, LocalTime tradingStart, LocalTime tradingEnd) {
        this.id = id;
        this.name = name;
        this.tradingStart = tradingStart;
        this.tradingEnd = tradingEnd;
    }
}
