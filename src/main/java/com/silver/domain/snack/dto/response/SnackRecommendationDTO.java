package com.silver.domain.snack.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SnackRecommendationDTO {
    private String name;
    private String reason;
    private String category;
    private String subCategory;  // 식품소분류명 필드 추가
    private String manufacturer; // 제조사명 필드 추가
    private String allergyInfo;
}