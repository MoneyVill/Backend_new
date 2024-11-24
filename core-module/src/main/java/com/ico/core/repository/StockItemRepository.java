package com.ico.core.repository;

import com.ico.core.entity.StockItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StockItemRepository extends JpaRepository<StockItem, Long> {
    List<StockItem> findAllByNationId(Long nationId);
    Optional<StockItem> findByNameAndNationId(String name, Long nationId);

}
