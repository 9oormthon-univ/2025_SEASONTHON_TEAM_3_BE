package com.silver.domain.user.repository;

import com.silver.domain.user.entity.UserPurpose;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserPurposeRepository extends JpaRepository<UserPurpose, Long> {
    List<UserPurpose> findAllByUserId(Long userId);
    boolean existsByUserIdAndPurposeId(Long userId, Long purposeId);
    void deleteByUserIdAndPurposeId(Long userId, Long purposeId);
}
