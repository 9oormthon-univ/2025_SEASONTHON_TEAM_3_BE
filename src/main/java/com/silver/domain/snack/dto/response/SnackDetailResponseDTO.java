package com.silver.domain.snack.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.silver.domain.snack.entity.Snack;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Builder
public class SnackDetailResponseDTO {

    // 기본 정보
    private String foodCode;
    private String name;
    private String snackCategory;
    private String manufacturer;
    private String foodWeight;
    private String servingSize;
    private String imageUrl;

    // 핵심 영양성분
    private Double energyKcal;
    private Double proteinG;
    private Double fatG;
    private Double carbohydrateG;
    private Double sugarG;
    private Double dietaryFiberG;
    private Double calciumMg;
    private Double ironMg;
    private Double potassiumMg;
    private Double sodiumMg;
    private Double vitaminARAEUg;
    private Double vitaminCMg;
    private Double cholesterolMg;
    private Double saturatedFatG;
    private Double transFatG;

    private final Set<String> hashtags;

    public static SnackDetailResponseDTO from(Snack snack , Set<String> hashtags) {
        return SnackDetailResponseDTO.builder()
                .foodCode(snack.getFoodCode())
                .name(snack.getName())
                .snackCategory(snack.getSnackCategory())
                .manufacturer(snack.getManufacturer())
                .foodWeight(snack.getFoodWeight())
                .servingSize(snack.getServingSize())
                .imageUrl(snack.getImageUrl())
                .energyKcal(snack.getEnergyKcal())
                .proteinG(snack.getProteinG())
                .fatG(snack.getFatG())
                .carbohydrateG(snack.getCarbohydrateG())
                .sugarG(snack.getSugarG())
                .dietaryFiberG(snack.getDietaryFiberG())
                .calciumMg(snack.getCalciumMg())
                .ironMg(snack.getIronMg())
                .potassiumMg(snack.getPotassiumMg())
                .sodiumMg(snack.getSodiumMg())
                .vitaminARAEUg(snack.getVitaminARAEUg())
                .vitaminCMg(snack.getVitaminCMg())
                .cholesterolMg(snack.getCholesterolMg())
                .saturatedFatG(snack.getSaturatedFatG())
                .transFatG(snack.getTransFatG())
                .hashtags(hashtags)
                .build();
    }
}