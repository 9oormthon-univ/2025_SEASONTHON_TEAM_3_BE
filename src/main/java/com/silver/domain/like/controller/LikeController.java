package com.silver.domain.like.controller;

import com.silver.domain.like.dto.response.LikeToggleResponseDto;
import com.silver.domain.like.dto.response.LikedSnackResponseDto;
import com.silver.domain.like.service.LikeService;
import com.silver.global.common.CustomApiResponse;
import com.silver.global.config.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Like", description = "찜(즐겨찾기) API")
@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @Operation(summary = "간식 찜/취소 토글")
    @PostMapping("/snacks/{snackId}")
    public ResponseEntity<CustomApiResponse<LikeToggleResponseDto>> toggleLike(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long snackId
    ) {
        LikeToggleResponseDto result = likeService.toggleLike(user.getId(), snackId);
        return ResponseEntity.ok(CustomApiResponse.onSuccess(result));
    }

    @Operation(summary = "마이페이지 - 내가 찜한 간식 전체 조회(비페이징)")
    @GetMapping("/snacks")
    public ResponseEntity<CustomApiResponse<List<LikedSnackResponseDto>>> getMyLikedSnacks(
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        List<LikedSnackResponseDto> list = likeService.getMyLikedSnacks(user.getId());
        return ResponseEntity.ok(CustomApiResponse.onSuccess(list));
    }
}
