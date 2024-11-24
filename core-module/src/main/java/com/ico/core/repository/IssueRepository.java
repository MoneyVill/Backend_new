package com.ico.core.repository;

import com.ico.core.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IssueRepository extends JpaRepository<Issue, Long> {
    // Find all issues for a given Stock, ordered by ID descending
    List<Issue> findAllByStockIdOrderByIdDesc(Long stockId);

    // Find all issues for a given Stock
    List<Issue> findAllByStockId(Long stockId);
}
