package com.silver.global.common;

import com.silver.domain.snack.entity.Snack;
import com.silver.domain.snack.repository.SnackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final SnackRepository snackRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (snackRepository.count() == 0) {
            System.out.println("데이터베이스가 비어있어 CSV 파일에서 데이터를 로드합니다...");
            loadDataFromCsv();
        } else {
            System.out.println("데이터베이스에 이미 데이터가 존재하므로, CSV 파일 로딩을 건너뜁니다.");
        }
    }

    private void loadDataFromCsv() {
        // 원본 CSV 파일을 참조합니다.
        ClassPathResource resource = new ClassPathResource("snacks_for_seniors_top100.csv");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String headerLine = br.readLine();
            if (headerLine == null) throw new Exception("CSV 파일이 비어있거나 헤더가 없습니다.");

            List<String> headers = Arrays.asList(headerLine.split(",", -1));

            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                String foodCode = getVal(values, headers.indexOf("식품코드"));
                String name = getVal(values, headers.indexOf("식품명"));
                String sourceOriginCode = getVal(values, headers.indexOf("식품기원코드"));
                String sourceOriginName = getVal(values, headers.indexOf("식품기원명"));
                String category = getVal(values, headers.indexOf("식품대분류명"));
                String foodMiddleCategory = getVal(values, headers.indexOf("식품중분류명"));
                String subCategory = getVal(values, headers.indexOf("식품소분류명"));
                String foodDetailCategory = getVal(values, headers.indexOf("식품세분류명"));
                // '1회 섭취참고량' 컬럼의 인덱스를 올바르게 찾습니다.
                String referenceServingSize = getVal(values, headers.indexOf("1회 섭취참고량"));
                Double energyKcal = parseDouble(getVal(values, headers.indexOf("에너지(kcal)")));
                Double proteinG = parseDouble(getVal(values, headers.indexOf("단백질(g)")));
                Double fatG = parseDouble(getVal(values, headers.indexOf("지방(g)")));
                Double carbohydrateG = parseDouble(getVal(values, headers.indexOf("탄수화물(g)")));
                Double sugarG = parseDouble(getVal(values, headers.indexOf("당류(g)")));
                Double dietaryFiberG = parseDouble(getVal(values, headers.indexOf("식이섬유(g)")));
                Double calciumMg = parseDouble(getVal(values, headers.indexOf("칼슘(mg)")));
                Double ironMg = parseDouble(getVal(values, headers.indexOf("철(mg)")));
                Double potassiumMg = parseDouble(getVal(values, headers.indexOf("칼륨(mg)")));
                Double sodiumMg = parseDouble(getVal(values, headers.indexOf("나트륨(mg)")));
                Double vitaminARAEUg = parseDouble(getVal(values, headers.indexOf("비타민A(μg RAE)")));
                Double vitaminCMg = parseDouble(getVal(values, headers.indexOf("비타민 C(mg)")));
                Double cholesterolMg = parseDouble(getVal(values, headers.indexOf("콜레스테롤(mg)")));
                Double saturatedFatG = parseDouble(getVal(values, headers.indexOf("포화지방산(g)")));
                Double transFatG = parseDouble(getVal(values, headers.indexOf("트랜스지방산(g)")));
                String foodWeight = getVal(values, headers.indexOf("식품중량"));
                String manufacturer = getVal(values, headers.indexOf("제조사명"));
                String importer = getVal(values, headers.indexOf("수입업체명"));
                String distributor = getVal(values, headers.indexOf("유통업체명"));
                String importedStatus = getVal(values, headers.indexOf("수입여부"));
                String countryOfOrigin = getVal(values, headers.indexOf("원산지국명"));
                String dataGenerationDate = getVal(values, headers.indexOf("데이터생성일자"));
                String dataStandardDate = getVal(values, headers.indexOf("데이터기준일자"));
                Double suitabilityScore = parseDouble(getVal(values, headers.indexOf("suitability_score")));

                if (name == null || name.trim().isEmpty()) continue;

                Snack snack = Snack.builder()
                        .foodCode(foodCode)
                        .name(name)
                        .category(category)
                        .subCategory(subCategory)
                        .servingSize(referenceServingSize)
                        .energyKcal(energyKcal)
                        .proteinG(proteinG)
                        .fatG(fatG)
                        .carbohydrateG(carbohydrateG)
                        .sugarG(sugarG)
                        .dietaryFiberG(dietaryFiberG)
                        .calciumMg(calciumMg)
                        .ironMg(ironMg)
                        .potassiumMg(potassiumMg)
                        .sodiumMg(sodiumMg)
                        .vitaminARAEUg(vitaminARAEUg)
                        .vitaminCMg(vitaminCMg)
                        .cholesterolMg(cholesterolMg)
                        .saturatedFatG(saturatedFatG)
                        .transFatG(transFatG)
                        .foodWeight(foodWeight)
                        .manufacturer(manufacturer)
                        .build();

                snackRepository.save(snack);
            }
        } catch (Exception e) {
            System.err.println("CSV 데이터 로딩 중 치명적인 오류가 발생했습니다.");
            e.printStackTrace();
        }
    }

    private String getVal(String[] values, int index) {
        if (index < 0 || index >= values.length || values[index].trim().isEmpty()) {
            return null;
        }
        String value = values[index].trim();
        return value.replace("\"", "");
    }

    private Double parseDouble(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}