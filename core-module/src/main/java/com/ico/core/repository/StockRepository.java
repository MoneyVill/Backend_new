package com.ico.core.repository;

import com.ico.core.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockRepository extends JpaRepository<Stock, Long> {
    // Find all stock issues for a given StockItem, ordered by ID descending
    List<Stock> findAllByStockItemIdOrderByIdDesc(Long stockItemId);

    // Find all stock issues for a given StockItem
    List<Stock> findAllByStockItemId(Long stockItemId);

    // New method to find all Stocks by Nation ID via StockItem
    List<Stock> findAllByStockItem_Nation_Id(Long nationId);

    // Optional: Delete all Stocks by Nation ID via StockItem
    void deleteAllByStockItem_Nation_Id(Long nationId);

    // Delete all stocks associated with a StockItem
    void deleteAllByStockItemId(Long stockItemId);

}
