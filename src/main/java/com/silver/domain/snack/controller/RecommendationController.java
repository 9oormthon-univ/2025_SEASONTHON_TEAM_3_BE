package com.silver.domain.snack.controller;

import com.silver.domain.snack.dto.request.SnackRequestDTO;
import com.silver.domain.snack.dto.response.SnackRecommendationResponseDTO;
import com.silver.domain.snack.service.RecommendationService;
import com.silver.global.common.CustomApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/recommend")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @Operation(summary = "맞춤 간식 추천", description = "사용자의 건강 정보, 선호도, 알레르기 등을 바탕으로 맞춤 간식을 추천합니다.")
    @PostMapping
    public ResponseEntity<CustomApiResponse<SnackRecommendationResponseDTO>> getSnackRecommendations(
            @RequestBody SnackRequestDTO requestDto) {
        try {
            SnackRecommendationResponseDTO recommendations = recommendationService.getSnackRecommendations(requestDto);
            return ResponseEntity.status(HttpStatus.OK).body(CustomApiResponse.onSuccess(recommendations));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CustomApiResponse.onFailure(e.getMessage(), "GEMINI_API_ERROR"));
        }
    }
}