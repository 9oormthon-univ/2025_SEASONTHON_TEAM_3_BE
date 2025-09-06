package com.silver.domain.snack.repository;

import com.silver.domain.snack.entity.Snack;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

// JpaSpecificationExecutor는 더 이상 필요 없으므로 삭제합니다.
public interface SnackRepository extends JpaRepository<Snack, Long> {

    // 1. 이름(키워드)으로 검색 (Paging 지원)
    Page<Snack> findByNameContaining(String keyword, Pageable pageable);

    // 2. 카테고리로 검색 (Paging 지원)
    Page<Snack> findBySnackCategory(String category, Pageable pageable);

    // 3. 이름(키워드)과 카테고리로 동시 검색 (Paging 지원)
    Page<Snack> findByNameContainingAndSnackCategory(String keyword, String category, Pageable pageable);

    // --- 기존 메소드들 ---
    List<Snack> findByCategoryIn(List<String> categories);
    long count();
    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT s.category FROM Snack s")
    List<String> findDistinctCategories();
    Optional<Snack> findByName(String name);
}