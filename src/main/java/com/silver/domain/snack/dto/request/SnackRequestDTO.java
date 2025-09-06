package com.silver.domain.snack.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SnackRequestDTO {
    // AI 추천에 사용할 간식 카테고리 필드
    private List<String> snackCategories;
}