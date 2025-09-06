package com.silver.domain.user.repository;

import com.silver.domain.user.entity.UserAllergy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserAllergyRepository extends JpaRepository<UserAllergy, Long> {
    List<UserAllergy> findAllByUserId(Long userId);
    boolean existsByUserIdAndAllergyId(Long userId, Long allergyId);
    void deleteByUserIdAndAllergyId(Long userId, Long allergyId);
}
