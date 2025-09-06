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
    private final String manufacturer;
    private final String snackCategory;
    private final Set<String> hashtags;
    private final String imageUrl;

    public static SnackResponseDTO from(Snack snack, Set<String> hashtags) {
        return SnackResponseDTO.builder()
                .id(snack.getId())
                .name(snack.getName())
                .manufacturer(snack.getManufacturer())
                .snackCategory(snack.getSnackCategory())
                .hashtags(hashtags)
                .imageUrl(snack.getImageUrl())
                .build();
    }
}