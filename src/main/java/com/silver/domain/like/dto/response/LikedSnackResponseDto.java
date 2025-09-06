package com.silver.domain.like.dto.response;

import lombok.*;

import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikedSnackResponseDto {
    private Long snackId;
    private String name;
    private String snackCategory;
    private String manufacturer;
    private Set<String> hashtags;
    private String imageUrl;
}
