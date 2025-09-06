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
import com.silver.domain.user.repository.UserAllergyRepository;
import com.silver.domain.user.repository.UserPurposeRepository;
import com.silver.global.config.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final GenerativeModel generativeModel;
    private final ObjectMapper objectMapper;
    private final SnackRepository snackRepository;
    private final UserAllergyRepository userAllergyRepository;
    private final UserPurposeRepository userPurposeRepository;

    public SnackRecommendationResponseDTO getSnackRecommendations(CustomUserDetails customUserDetails, SnackRequestDTO requestDto) throws IOException {
        Long userId = customUserDetails.getId();

        // 1. 사용자 정보 조회
        Set<String> userAllergies = userAllergyRepository.findAllByUserId(userId).stream()
                .map(ua -> ua.getAllergy().getName())
                .collect(Collectors.toSet());

        Set<String> userPurposes = userPurposeRepository.findAllByUserId(userId).stream()
                .map(up -> up.getPurpose().getName())
                .collect(Collectors.toSet());

        // 2. 추천 대상 카테고리 선정
        List<String> targetCategories;
        if (requestDto.getSnackCategories() == null || requestDto.getSnackCategories().isEmpty()) {
            // 카테고리 요청이 없으면, 안전한 카테고리를 랜덤으로 선택
            List<String> allCategories = snackRepository.findDistinctSnackCategories();
            List<String> safeCategories = filterSafeCategories(allCategories, userAllergies); // 수정된 로직 사용
            Collections.shuffle(safeCategories);
            targetCategories = safeCategories.stream().limit(4).collect(Collectors.toList());
        } else {
            targetCategories = requestDto.getSnackCategories();
        }

        if (targetCategories.isEmpty()){
            return new SnackRecommendationResponseDTO(Collections.emptyList());
        }

        // 3. 선택된 카테고리의 모든 간식 조회
        List<Snack> initialCandidates = snackRepository.findBySnackCategoryIn(targetCategories);

        // 4. 제품명/카테고리명을 기준으로 2차 상세 필터링
        List<Snack> safeCandidates = filterSnacksByAllergies(initialCandidates, userAllergies);

        if (safeCandidates.isEmpty()) {
            return new SnackRecommendationResponseDTO(Collections.emptyList());
        }

        // 5. AI를 통해 추천 받기
        List<SnackRecommendationDTO> aiRecommendations = getAiRecommendations(userAllergies, userPurposes, safeCandidates);

        // 6. 최종 데이터 조립
        Map<Long, Snack> snackMap = snackRepository.findAllById(
                aiRecommendations.stream().map(SnackRecommendationDTO::getId).collect(Collectors.toList())
        ).stream().collect(Collectors.toMap(Snack::getId, Function.identity()));

        List<SnackRecommendationDTO> finalRecommendations = aiRecommendations.stream()
                .filter(rec -> snackMap.containsKey(rec.getId()))
                .map(rec -> {
                    Snack snack = snackMap.get(rec.getId());
                    return SnackRecommendationDTO.from(snack, rec.getReason(), rec.getAllergyInfo());
                })
                .collect(Collectors.toList());

        return new SnackRecommendationResponseDTO(finalRecommendations);
    }



     //카테고리 목록에서 '높은 위험도'의 알레르기 관련 카테고리만 제외
    private List<String> filterSafeCategories(List<String> allCategories, Set<String> allergies) {
        if (allergies == null || allergies.isEmpty()) {
            return allCategories;
        }

        // 카테고리 전체가 알레르기와 직접적으로 강하게 연관된 경우만 매핑
        Map<String, List<String>> highRiskCategoryMap = Map.of(
                "우유", List.of("유제품"),
                "밀", List.of("과자·빵·떡", "면류")
        );

        return allCategories.stream()
                .filter(category -> {
                    for (String allergy : allergies) {
                        List<String> relatedCategories = highRiskCategoryMap.get(allergy);
                        if (relatedCategories != null && relatedCategories.contains(category)) {
                            return false; // 높은 위험도의 카테고리면 제외
                        }
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    //제품명과 카테고리명을 기준으로 상세 필터링을 수행
    private List<Snack> filterSnacksByAllergies(List<Snack> snacks, Set<String> allergies) {
        if (allergies == null || allergies.isEmpty()) {
            return snacks;
        }

        Map<String, List<String>> allergyKeywords = Map.ofEntries(
                Map.entry("우유", List.of("우유", "유제품", "치즈", "요거트", "요구르트", "라떼", "크림", "버터")),
                Map.entry("달걀", List.of("달걀", "계란", "난류", "에그", "마요네즈")),
                Map.entry("밀", List.of("밀", "밀가루", "빵", "면", "국수", "과자", "쿠키", "케이크", "보리")),
                Map.entry("대두", List.of("대두", "두유", "콩", "된장", "고추장", "간장", "두부")),
                Map.entry("땅콩", List.of("땅콩", "피넛")),
                Map.entry("견과류", List.of("견과", "아몬드", "호두", "잣", "캐슈넛", "피스타치오", "마카다미아")),
                Map.entry("생선", List.of("생선", "어묵", "참치", "고등어", "연어", "멸치", "어류")),
                Map.entry("조개.갑각류", List.of("조개", "갑각류", "새우", "게", "굴", "홍합", "오징어", "문어", "해물", "해산물")),
                Map.entry("참깨", List.of("참깨", "깨")),
                Map.entry("메밀", List.of("메밀", "면류"))
        );

        return snacks.stream()
                .filter(snack -> {
                    for (String allergy : allergies) {
                        List<String> keywords = allergyKeywords.get(allergy);
                        if (keywords != null) {
                            for (String keyword : keywords) {
                                if (snack.getName().contains(keyword) || snack.getSnackCategory().contains(keyword)) {
                                    return false;
                                }
                            }
                        }
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    private List<SnackRecommendationDTO> getAiRecommendations(Set<String> userAllergies, Set<String> userPurposes, List<Snack> candidateSnacks) throws IOException {
        // AI에게는 ID, 이유, 알러지 정보만 생성하도록 요청
        String prompt = createFinalRecommendationPrompt(userAllergies, userPurposes, candidateSnacks);
        GenerateContentResponse response = this.generativeModel.generateContent(prompt);
        String responseText = ResponseHandler.getText(response);
        String cleanJson = responseText.replace("```json", "").replace("```", "").trim();
        // AI 응답은 id, reason, allergyInfo 필드만 가짐
        return objectMapper.readValue(cleanJson, new TypeReference<List<SnackRecommendationDTO>>() {});
    }

    private String createFinalRecommendationPrompt(Set<String> userAllergies, Set<String> userPurposes, List<Snack> candidateSnacks) {
        String snackListString = candidateSnacks.stream()
                .map(snack -> String.format(
                        "- ID: %d, 제품명: %s, 제조사: %s, 카테고리: %s, 총당류: %.2fg, 나트륨: %.2fmg, 단백질: %.2fg",
                        snack.getId(), snack.getName(), snack.getManufacturer(), snack.getSnackCategory(),
                        snack.getSugarG(), snack.getSodiumMg(), snack.getProteinG()))
                .collect(Collectors.joining("\n"));

        // 추천할 제품 수를 후보군 크기에 맞춰 동적으로 조정
        int recommendationCount = Math.min(candidateSnacks.size(), 6);

        // [핵심 수정 부분] 새로운 건강 목적 규칙 추가
        StringBuilder rulesBuilder = new StringBuilder();
        if (userPurposes != null && !userPurposes.isEmpty()) {
            if (userPurposes.contains("혈당"))
                rulesBuilder.append("\n- 혈당 관리를 위해 '당류(g)'가 낮은 제품을 우선적으로 고려해야 합니다.");
            if (userPurposes.contains("혈압"))
                rulesBuilder.append("\n- 혈압 관리를 위해 '나트륨(mg)' 함량이 낮은 제품을 우선적으로 고려해야 합니다.");
            if (userPurposes.contains("콜레스테롤"))
                rulesBuilder.append("\n- 콜레스테롤 관리를 위해 '콜레스테롤(mg)'과 '포화지방산(g)' 함량이 낮은 제품을 우선적으로 고려해야 합니다.");
            if (userPurposes.contains("체중 관리"))
                rulesBuilder.append("\n- 체중 관리를 위해 '에너지(kcal)'와 '지방(g)' 함량이 낮은 제품을 우선적으로 고려해야 합니다.");
            if (userPurposes.contains("신장"))
                rulesBuilder.append("\n- 신장 건강을 위해 '나트륨(mg)'과 '단백질(g)' 함량이 낮은 제품을 우선적으로 고려해야 합니다.");
            if (userPurposes.contains("심혈관"))
                rulesBuilder.append("\n- 심혈관 건강을 위해 '나트륨(mg)', '포화지방산(g)', '콜레스테롤(mg)' 함량이 낮은 제품을 우선적으로 고려해야 합니다.");
        }

        if (userAllergies != null && !userAllergies.isEmpty()) {
            rulesBuilder.append("\n- 사용자가 알레르기가 있는 성분(")
                    .append(String.join(", ", userAllergies))
                    .append(")이 함유된 제품은 절대로 추천해서는 안 됩니다.");
        }
        rulesBuilder.append("\n- 각 추천 항목에는 반드시 위 '추천 가능한 전체 제품 목록'에 표시된 정수형 ID를 그대로 포함해야 합니다.");
        rulesBuilder.append("\n- 절대로 같은 제품을 중복해서 추천하면 안 됩니다."); // 중복 추천 방지 규칙 추가

        return "당신은 주어진 제품 목록 내에서 사용자의 건강 정보에 가장 적합한 제품을 추천하는 최고의 영양사입니다." +
                "\n\n### 사용자 정보" +
                "\n- 등록된 알레르기 정보: " + formatSet(userAllergies) +
                "\n- 등록된 건강 목표: " + formatSet(userPurposes) +
                "\n\n### 추천 가능한 전체 제품 목록 (선택된 카테고리들)" +
                "\n" + snackListString +
                "\n\n### 추천 시 반드시 지켜야 할 규칙" +
                rulesBuilder.toString() +
                "\n\n위 사용자 정보와 규칙을 모두 고려하여, '추천 가능한 전체 제품 목록' 안에서만 사용자에게 가장 적합한 간식을 총 " + recommendationCount + "개 추천해주세요. 다른 설명은 일절 포함하지 말고 아래 JSON 배열 형식으로만 응답해주세요." +
                "\n[" +
                "\n  {" +
                "\n    \"id\": 123," + // AI는 ID만 제공
                "\n    \"reason\": \"사용자의 건강 정보와 제품의 영양성분을 종합적으로 고려하여 추천하는 이유를 구체적으로 작성\"," +
                "\n    \"allergyInfo\": \"제품에 포함된 주요 알레르기 유발 물질 (DB에 정보가 없으니 영양성분, 이름 등을 보고 판단)\"" +
                "\n  }" +
                "\n]";
    }

    private String formatSet(Set<String> set) {
        if (set == null || set.isEmpty()) {
            return "해당 없음";
        }
        return String.join(", ", set);
    }
}