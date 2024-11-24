package com.ico.core.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Issue {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String content; // Issue content

    private double amount; // Price or value

    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    @Builder
    public Issue(Long id, String content, double amount, LocalDateTime date, Stock stock) {
        this.id = id;
        this.content = content;
        this.amount = amount;
        this.date = date;
        this.stock = stock;
    }
}
