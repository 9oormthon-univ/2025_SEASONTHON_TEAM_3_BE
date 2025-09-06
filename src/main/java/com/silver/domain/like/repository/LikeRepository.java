package com.silver.domain.like.repository;

import com.silver.domain.like.entity.Like;
import com.silver.domain.snack.entity.Snack;
import com.silver.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Long> {

    boolean existsByUserAndSnack(User user, Snack snack);
    void deleteByUserAndSnack(User user, Snack snack);
    long countBySnack(Snack snack);
    List<Like> findByUserOrderByIdDesc(User user);
}
