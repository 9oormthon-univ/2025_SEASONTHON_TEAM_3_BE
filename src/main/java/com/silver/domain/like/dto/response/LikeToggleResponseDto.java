package com.silver.domain.like.dto.response;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeToggleResponseDto {
    private boolean liked;
    private long likeCount;
}
