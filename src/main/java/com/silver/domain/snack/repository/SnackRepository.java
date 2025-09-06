package com.silver.domain.snack.repository;

import com.silver.domain.snack.entity.Snack;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface SnackRepository extends JpaRepository<Snack, Long> {

    // --- 새로운 통합 검색 메서드 추가 ---
    @Query("SELECT s FROM Snack s " +
            "LEFT JOIN SnackHashtag sh ON s.id = sh.snack.id " +
            "LEFT JOIN Hashtag h ON sh.hashtag.id = h.id " +
            "WHERE (COALESCE(:keyword, '') = '' OR s.name LIKE %:keyword%) " +
            "AND (COALESCE(:category, '') = '' OR s.snackCategory = :category) " +
            "AND (:hasHashtags = false OR h.name IN :hashtags) " +
            "GROUP BY s.id")
    Page<Snack> findSnacksByFilters(
            @Param("keyword") String keyword,
            @Param("category") String category,
            @Param("hashtags") List<String> hashtags,
            @Param("hasHashtags") boolean hasHashtags, // 해시태그 파라미터 유무를 전달하기 위한 플래그
            Pageable pageable
    );


    // --- 기존 메소드들 (이제 사용되지 않지만, 다른 곳에서 사용될 수 있으니 유지) ---
    Page<Snack> findByNameContaining(String keyword, Pageable pageable);
    Page<Snack> findBySnackCategory(String category, Pageable pageable);
    Page<Snack> findByNameContainingAndSnackCategory(String keyword, String category, Pageable pageable);
    List<Snack> findByCategoryIn(List<String> categories);
    long count();
    @Query("SELECT DISTINCT s.category FROM Snack s")
    List<String> findDistinctCategories();
    Optional<Snack> findByName(String name);
}