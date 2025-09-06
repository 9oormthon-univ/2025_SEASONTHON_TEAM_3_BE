package com.silver.domain.snack.dto.response;

import com.silver.domain.snack.entity.Snack; // import 추가
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SnackRecommendationDTO {
    // AI로부터 직접 받을 필드
    private Long id;
    private String reason;

    // DB 조회 후 채워넣을 필드
    private String name;
    private String manufacturer;
    private String snackCategory;
    private String allergyInfo; // 이 필드는 AI가 생성하도록 유지
    private String imageUrl;

    // 정적 팩토리 메서드 추가
    public static SnackRecommendationDTO from(Snack snack, String reason, String allergyInfo) {
        SnackRecommendationDTO dto = new SnackRecommendationDTO();
        dto.setId(snack.getId());
        dto.setName(snack.getName());
        dto.setManufacturer(snack.getManufacturer());
        dto.setSnackCategory(snack.getSnackCategory());
        dto.setImageUrl(snack.getImageUrl());
        dto.setReason(reason); // AI가 생성한 이유
        dto.setAllergyInfo(allergyInfo); // AI가 생성한 알러지 정보
        return dto;
    }
}