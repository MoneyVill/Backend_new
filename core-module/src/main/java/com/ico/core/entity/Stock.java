package com.ico.core.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

/**
 * @author 변윤경
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "stock_item_id")
    private StockItem stockItem;

    private double amount; // Price or value

    private String content; // Issue content

    private LocalDateTime date;

    @Builder
    public Stock(Long id, StockItem stockItem, double amount, String content, LocalDateTime date) {
        this.id = id;
        this.stockItem = stockItem;
        this.amount = amount;
        this.content = content;
        this.date = date;
    }
}

