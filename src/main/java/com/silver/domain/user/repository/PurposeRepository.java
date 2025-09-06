package com.silver.domain.user.repository;

import com.silver.domain.user.entity.Purpose;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PurposeRepository extends JpaRepository<Purpose, Long> {
    Optional<Purpose> findByCode(String code);
}
