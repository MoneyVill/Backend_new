package com.ico.core.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class StockItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name; // Stock item name

    @ManyToOne
    @JoinColumn(name = "nation_id")
    private Nation nation;

    private LocalTime tradingStart;

    private LocalTime tradingEnd;

    @OneToMany(mappedBy = "stockItem", cascade = CascadeType.ALL)
    private List<Stock> stockIssues;

    @Builder
    public StockItem(Long id, String name, Nation nation, LocalTime tradingStart, LocalTime tradingEnd) {
        this.id = id;
        this.name = name;
        this.nation = nation;
        this.tradingStart = tradingStart;
        this.tradingEnd = tradingEnd;
    }
}
