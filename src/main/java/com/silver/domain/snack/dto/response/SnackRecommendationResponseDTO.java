package com.silver.domain.snack.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SnackRecommendationResponseDTO {
    private List<SnackRecommendationDTO> recommendations;
    private Set<String> subCategories;
}