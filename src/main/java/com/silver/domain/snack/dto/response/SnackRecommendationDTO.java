package com.silver.domain.snack.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SnackRecommendationDTO {
    private Long id;
    private String name;
    private String reason;
    private String category;
    private String subCategory;
    private String manufacturer;
    private String snackCategory;
    private String allergyInfo;
}