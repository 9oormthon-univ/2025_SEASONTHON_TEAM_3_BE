package com.silver.domain.snack.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Snack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- 기본 정보 ---
    @Column(name = "food_code")
    private String foodCode;         // 식품코드

    @Column(name = "name", nullable = false, length = 1024)
    private String name;             // 식품명

    @Column(name = "category", length = 1024)
    private String category;         // 식품대분류명

    @Column(name = "sub_category", length = 1024)
    private String subCategory;      // 식품소분류명

    @Column(name = "manufacturer", length = 1024)
    private String manufacturer;     // 제조사명

    @Column(name = "food_weight", length = 1024)
    private String foodWeight;       // 식품중량 (단위가 포함된 문자열이므로 String 타입)

    // --- 핵심 영양성분 ---
    @Column(name = "reference_serving_size", length = 1024)
    private String servingSize; // 1회 섭취참고량 (g)

    @Column(name = "energy_kcal")
    private Double energyKcal;       // 에너지 (kcal)

    @Column(name = "protein_g")
    private Double proteinG;         // 단백질 (g)

    @Column(name = "fat_g")
    private Double fatG;             // 지방 (g)

    @Column(name = "carbohydrate_g")
    private Double carbohydrateG;    // 탄수화물 (g)

    @Column(name = "sugar_g")
    private Double sugarG;           // 당류 (g)

    @Column(name = "dietary_fiber_g")
    private Double dietaryFiberG;    // 식이섬유 (g)

    @Column(name = "calcium_mg")
    private Double calciumMg;        // 칼슘 (mg)

    @Column(name = "iron_mg")
    private Double ironMg;           // 철 (mg)

    @Column(name = "potassium_mg")
    private Double potassiumMg;      // 칼륨 (mg)

    @Column(name = "sodium_mg")
    private Double sodiumMg;         // 나트륨 (mg)

    @Column(name = "vitamin_a_rae_ug")
    private Double vitaminARAEUg;    // 비타민A (μg RAE)

    @Column(name = "vitamin_c_mg")
    private Double vitaminCMg;       // 비타민 C (mg)

    @Column(name = "cholesterol_mg")
    private Double cholesterolMg;    // 콜레스테롤 (mg)

    @Column(name = "saturated_fat_g")
    private Double saturatedFatG;    // 포화지방산 (g)

    @Column(name = "trans_fat_g")
    private Double transFatG;        // 트랜스지방산 (g)


    @Builder
    public Snack(String foodCode, String name, String category, String subCategory, String manufacturer, String foodWeight,
                 String servingSize, Double energyKcal, Double proteinG, Double fatG, Double carbohydrateG, Double sugarG,
                 Double dietaryFiberG, Double calciumMg, Double ironMg, Double potassiumMg, Double sodiumMg, Double vitaminARAEUg,
                 Double vitaminCMg, Double cholesterolMg, Double saturatedFatG, Double transFatG) {
        this.foodCode = foodCode;
        this.name = name;
        this.category = category;
        this.subCategory = subCategory;
        this.manufacturer = manufacturer;
        this.foodWeight = foodWeight;
        this.servingSize = servingSize;
        this.energyKcal = energyKcal;
        this.proteinG = proteinG;
        this.fatG = fatG;
        this.carbohydrateG = carbohydrateG;
        this.sugarG = sugarG;
        this.dietaryFiberG = dietaryFiberG;
        this.calciumMg = calciumMg;
        this.ironMg = ironMg;
        this.potassiumMg = potassiumMg;
        this.sodiumMg = sodiumMg;
        this.vitaminARAEUg = vitaminARAEUg;
        this.vitaminCMg = vitaminCMg;
        this.cholesterolMg = cholesterolMg;
        this.saturatedFatG = saturatedFatG;
        this.transFatG = transFatG;
    }
}