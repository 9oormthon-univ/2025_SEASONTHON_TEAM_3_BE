package com.silver.domain.like.service;

import com.silver.domain.like.dto.response.LikeToggleResponseDto;
import com.silver.domain.like.dto.response.LikedSnackResponseDto;

import java.util.List;

public interface LikeService {
    LikeToggleResponseDto toggleLike(Long userId, Long snackId);
    List<LikedSnackResponseDto> getMyLikedSnacks(Long userId);
}
