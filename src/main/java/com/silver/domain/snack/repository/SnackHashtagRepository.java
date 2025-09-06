package com.silver.domain.snack.repository;

import com.silver.domain.snack.entity.SnackHashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SnackHashtagRepository extends JpaRepository<SnackHashtag, Long> {

    @Query("""
        select h.name
        from SnackHashtag sh
        join sh.hashtag h
        where sh.snack.id = :snackId
    """)
    List<String> findHashtagNamesBySnackId(@Param("snackId") Long snackId);

}
