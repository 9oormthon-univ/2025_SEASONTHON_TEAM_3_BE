package com.silver.domain.snack.controller;

import com.silver.domain.snack.dto.response.SnackDetailResponseDTO;
import com.silver.domain.snack.dto.response.SnackResponseDTO;
import com.silver.domain.snack.service.SnackService;
import com.silver.global.common.CustomApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/snacks")
@RequiredArgsConstructor
public class SnackController {

    private final SnackService snackService;

    @Operation(summary = "전체 간식 페이징 조회", description = "데이터베이스에 저장된 모든 간식 정보를 페이지 형태로 조회합니다.")
    @GetMapping
    public ResponseEntity<CustomApiResponse<Page<SnackResponseDTO>>> getAllSnacks(Pageable pageable) {
        Page<SnackResponseDTO> snackPage = snackService.getAllSnacks(pageable);
        return ResponseEntity.ok(CustomApiResponse.onSuccess(snackPage));
    }

    @Operation(summary = "간식 상세 정보 조회", description = "간식 ID를 통해 특정 간식의 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<CustomApiResponse<SnackDetailResponseDTO>> getSnackById(@PathVariable Long id) {
        SnackDetailResponseDTO snackDetail = snackService.getSnackById(id);
        return ResponseEntity.ok(CustomApiResponse.onSuccess(snackDetail));
    }
}