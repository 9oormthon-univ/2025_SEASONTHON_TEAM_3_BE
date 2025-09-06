package com.silver.domain.snack.dto.response;

import com.silver.domain.snack.entity.Snack;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Builder
public class SnackResponseDTO {

    private final Long id;
    private final String name;
    private final String category;
    private final String subCategory;
    private final String manufacturer;
    private final String servingSize;

    private final Set<String> hashtags;

    public static SnackResponseDTO from(Snack snack, Set<String> hashtags) {
        return SnackResponseDTO.builder()
                .id(snack.getId())
                .name(snack.getName())
                .category(snack.getCategory())
                .subCategory(snack.getSubCategory())
                .manufacturer(snack.getManufacturer())
                .servingSize(snack.getServingSize())
                .hashtags(hashtags)
                .build();
    }
}