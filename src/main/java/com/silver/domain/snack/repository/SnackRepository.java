package com.silver.domain.snack.repository;

import com.silver.domain.snack.entity.Snack;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SnackRepository extends JpaRepository<Snack, Long> {
    // 특정 카테고리에 해당하는 모든 과자를 찾는 메소드
    List<Snack> findByCategoryIn(List<String> categories);

    // DB에 데이터가 있는지 확인하기 위한 카운트 메소드
    long count();

    // 전체 카테고리 목록을 중복 없이 조회하는 쿼리 (JPQL)
    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT s.category FROM Snack s")
    List<String> findDistinctCategories();
}