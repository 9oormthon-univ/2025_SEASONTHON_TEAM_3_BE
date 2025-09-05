package com.silver.domain.snack.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import com.silver.domain.snack.dto.request.SnackRequestDTO;
import com.silver.domain.snack.dto.response.SnackRecommendationDTO;
import com.silver.domain.snack.dto.response.SnackRecommendationResponseDTO;
import com.silver.domain.snack.entity.Snack;
import com.silver.domain.snack.repository.SnackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final GenerativeModel generativeModel;
    private final ObjectMapper objectMapper;
    private final SnackRepository snackRepository;

    public SnackRecommendationResponseDTO getSnackRecommendations(SnackRequestDTO requestDto) throws IOException {
        List<String> recommendedCategories = recommendCategories(requestDto);
        List<Snack> candidateSnacks = snackRepository.findByCategoryIn(recommendedCategories);
        List<SnackRecommendationDTO> recommendations = getFinalRecommendations(requestDto, candidateSnacks);

        Set<String> subCategories = recommendations.stream()
                .map(SnackRecommendationDTO::getSubCategory)
                .collect(Collectors.toSet());

        return new SnackRecommendationResponseDTO(recommendations, subCategories);
    }

    private List<String> recommendCategories(SnackRequestDTO requestDto) throws IOException {
        List<String> allCategories = snackRepository.findDistinctCategories();
        String prompt = createCategoryPrompt(requestDto, allCategories);
        GenerateContentResponse response = this.generativeModel.generateContent(prompt);
        String responseText = ResponseHandler.getText(response);
        String cleanJson = responseText.replace("```json", "").replace("```", "").trim();
        return objectMapper.readValue(cleanJson, new TypeReference<List<String>>() {});
    }

    private List<SnackRecommendationDTO> getFinalRecommendations(SnackRequestDTO requestDto, List<Snack> candidateSnacks) throws IOException {
        String prompt = createFinalRecommendationPrompt(requestDto, candidateSnacks);
        GenerateContentResponse response = this.generativeModel.generateContent(prompt);
        String responseText = ResponseHandler.getText(response);
        String cleanJson = responseText.replace("```json", "").replace("```", "").trim();
        return objectMapper.readValue(cleanJson, new TypeReference<List<SnackRecommendationDTO>>() {});
    }

    private String createCategoryPrompt(SnackRequestDTO dto, List<String> allCategories) {
        String allCategoriesString = String.join(", ", allCategories);
        return "당신은 사용자의 건강 데이터를 분석하여 가장 적합한 식품 카테고리를 추천해야 합니다." +
                "\n\n### 사용자 정보 및 요청사항" +
                "\n- 건강 우려사항: " + formatList(dto.getHealthConcerns()) +
                "\n- 신체적 특징: " + formatList(dto.getPhysicalConditions()) +
                "\n- 기타 제한사항: " + formatList(dto.getDietaryRestrictions()) +
                "\n- 선호 키워드: " + formatList(dto.getKeywords()) +
                "\n\n### 추천 가능한 전체 카테고리 목록" +
                "\n" + allCategoriesString +
                "\n\n위 사용자 정보와 전체 카테고리 목록을 참고하여, 이 사용자에게 가장 적합한 카테고리 4개만 골라주세요. 다른 설명은 일절 포함하지 말고, 반드시 아래와 같은 JSON 배열 형식으로만 응답해주세요." +
                "\n[\"추천카테고리1\", \"추천카테고리2\", \"추천카테고리3\", \"추천카테고리4\"]";
    }

    private String createFinalRecommendationPrompt(SnackRequestDTO dto, List<Snack> candidateSnacks) {
        String snackListString = candidateSnacks.stream()
                .map(snack -> String.format("- 제품명: %s, 카테고리: %s, 소분류: %s, 제조사: %s, 총당류: %.2fg, 나트륨: %.2fmg",
                        snack.getName(), snack.getCategory(), snack.getSubCategory(), snack.getManufacturer(), snack.getSugarG(), snack.getSodiumMg()))
                .collect(Collectors.joining("\n"));

        StringBuilder rulesBuilder = new StringBuilder();
        if (dto.getHealthConcerns() != null && !dto.getHealthConcerns().isEmpty()) {
            if (dto.getHealthConcerns().contains("혈당 관리")) rulesBuilder.append("\n- 혈당 관리를 위해 '총당류(sugarG)'가 낮은 제품을 우선적으로 고려해야 합니다.");
            if (dto.getHealthConcerns().contains("혈압 관리")) rulesBuilder.append("\n- 혈압 관리를 위해 '나트륨(sodiumMg)' 함량이 낮은 제품을 우선적으로 고려해야 합니다.");
        }
        rulesBuilder.append("\n- 반드시 완제품 형태의 제품만 추천해야 하며, '밀가루'와 같은 재료 자체를 추천해서는 안 됩니다.");
        rulesBuilder.append("\n- JSON 응답의 'subCategory' 필드에는 제품의 소분류명을 기입합니다. 만약 제품의 소분류가 없거나 '해당없음'인 경우, 'category' 값을 대신 기입해야 합니다.");


        return "당신은 주어진 제품 목록 내에서 사용자의 요구사항에 가장 적합한 제품을 추천하는 최고의 영양사입니다." +
                "\n\n### 사용자 정보 및 요청사항" +
                "\n- 건강 우려사항: " + formatList(dto.getHealthConcerns()) +
                "\n- 신체적 특징: " + formatList(dto.getPhysicalConditions()) +
                "\n- 기타 제한사항: " + formatList(dto.getDietaryRestrictions()) +
                "\n- 선호 키워드: " + formatList(dto.getKeywords()) +
                "\n\n### 추천 가능한 전체 제품 목록" +
                "\n" + snackListString +
                "\n\n### 추천 시 반드시 지켜야 할 규칙" +
                rulesBuilder.toString() +
                "\n\n위 사용자 정보와 규칙을 모두 고려하여, '추천 가능한 전체 제품 목록' 안에서만 간식을 총 8개 추천해주세요. 각 카테고리마다 반드시 2개씩 추천해야 합니다. 다른 설명은 일절 포함하지 말고 아래 JSON 배열 형식으로만 응답해주세요." +
                "\n[" +
                "\n  {" +
                "\n    \"name\": \"추천 제품명\"," +
                "\n    \"reason\": \"사용자의 모든 정보와 제품의 영양성분을 종합적으로 고려하여 추천하는 이유를 구체적으로 작성\"," +
                "\n    \"category\": \"간식 대분류명\"," +
                "\n    \"subCategory\": \"간식 소분류명\"," +
                "\n    \"manufacturer\": \"제품 제조사명\"," +
                "\n    \"allergyInfo\": \"제품에 포함된 주요 알레르기 유발 물질 (DB에 정보가 있다면 그걸 사용)\"" +
                "\n  }" +
                "\n]";
    }

    private String formatList(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "해당 없음";
        }
        return String.join(", ", list);
    }
}