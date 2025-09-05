package com.silver.global.common;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.silver.domain.snack.entity.Snack;
import com.silver.domain.snack.repository.SnackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SnackRepository snackRepository;

    @Override
    @Transactional
    public void run(String... args) throws IOException, CsvValidationException {
        if (snackRepository.count() > 0) {
            System.out.println("데이터베이스에 이미 데이터가 존재합니다. 초기화를 건너뜁니다.");
            return;
        }

        String csvFilePath = "data/snack_data.csv";
        ClassPathResource resource = new ClassPathResource(csvFilePath);

        // OpenCSV의 CSVReader를 사용하여 파일을 안정적으로 읽습니다.
        try (CSVReader reader = new CSVReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

            // 헤더 행은 건너뜁니다.
            reader.readNext();

            String[] data;
            int rowNum = 1; // 오류 추적을 위한 행 번호
            while ((data = reader.readNext()) != null) {
                rowNum++;
                Snack snack = createSnackFromCsvRow(data, rowNum);
                if (snack != null) {
                    snackRepository.save(snack);
                }
            }
        }
        System.out.println("CSV 데이터를 데이터베이스에 성공적으로 로드했습니다.");
    }

    private Snack createSnackFromCsvRow(String[] data, int rowNum) {
        try {
            // 이 부분의 로직은 동일하지만, 이제 data 배열이 정확하게 파싱되어 들어옵니다.
            return Snack.builder()
                    .foodCode(getString(data, 0))         // 식품코드
                    .name(getString(data, 1))             // 식품명
                    .category(getString(data, 7))         // 식품대분류명
                    .subCategory(getString(data, 13))      // 식품소분류명
                    .referenceServingSize(getDouble(data, 157)) // 1회 섭취참고량 (g)
                    .foodWeight(getString(data, 158))      // 식품중량
                    .manufacturer(getString(data, 160))   // 제조사명
                    .energyKcal(getDouble(data, 17))      // 에너지(kcal)
                    .moistureG(getDouble(data, 18))       // 수분(g)
                    .proteinG(getDouble(data, 19))      // 단백질(g)
                    .fatG(getDouble(data, 20))           // 지방(g)
                    .carbohydrateG(getDouble(data, 22))   // 탄수화물(g)
                    .sugarG(getDouble(data, 23))        // 당류(g)
                    .sodiumMg(getDouble(data, 28))        // 나트륨(mg)
                    .cholesterolMg(getDouble(data, 38))   // 콜레스테롤(mg)
                    .saturatedFatG(getDouble(data, 39)) // 포화지방산(g)
                    .transFatG(getDouble(data, 40))     // 트랜스지방산(g)
                    .build();
        } catch (Exception e) {
            System.err.println("CSV 행 처리 중 오류 발생 (행 번호: " + rowNum + "), 내용: " + String.join(",", data) + ", 오류: " + e.getMessage());
            return null;
        }
    }

    private String getString(String[] data, int index) {
        if (index >= data.length || data[index] == null || data[index].isEmpty() || data[index].equalsIgnoreCase("NULL")) {
            return null;
        }
        return data[index];
    }

    private Double getDouble(String[] data, int index) {
        if (index >= data.length || data[index] == null || data[index].isEmpty() || data[index].equalsIgnoreCase("NULL")) {
            return null;
        }
        try {
            return Double.parseDouble(data[index]);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}