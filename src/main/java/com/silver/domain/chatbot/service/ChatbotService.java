package com.silver.domain.chatbot.service;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import com.silver.domain.user.repository.UserAllergyRepository;
import com.silver.domain.user.repository.UserPurposeRepository;
import com.silver.global.config.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatbotService {

    private final GenerativeModel generativeModel;
    private final UserAllergyRepository userAllergyRepository;
    private final UserPurposeRepository userPurposeRepository;

    @Async
    @Transactional(readOnly = true)
    // 파라미터를 ChatMessageRequest 대신 String userMessage로 직접 받도록 수정
    public void streamAiResponse(CustomUserDetails userDetails, String userMessage, SseEmitter emitter) {
        // 1. 사용자 정보 조회
        Set<String> allergies = userAllergyRepository.findAllByUserId(userDetails.getId()).stream()
                .map(ua -> ua.getAllergy().getName())
                .collect(Collectors.toSet());
        Set<String> purposes = userPurposeRepository.findAllByUserId(userDetails.getId()).stream()
                .map(up -> up.getPurpose().getName())
                .collect(Collectors.toSet());

        // 2. 대화형 프롬프트 생성 (대화 기록 로직 제거)
        String prompt = createPromptForChat(userMessage, allergies, purposes);

        try {
            // 3. AI 응답 스트리밍 시작
            generativeModel.generateContentStream(prompt).forEach(partialResponse -> {
                try {
                    String textChunk = ResponseHandler.getText(partialResponse);
                    emitter.send(SseEmitter.event().data(textChunk));
                } catch (IOException e) {
                    log.error("SSE Emitter send error", e);
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            log.error("Error during AI content generation stream", e);
            emitter.completeWithError(e);
        } finally {
            emitter.complete();
        }
    }

    /**
     * [업데이트된 메서드]
     * 이전 대화 기록(history) 관련 로직을 모두 제거하여 프롬프트를 단순화했습니다.
     */
    private String createPromptForChat(String userMessage, Set<String> allergies, Set<String> purposes) {
        String userInfo = String.format("\n- 사용자의 알레르기 정보: %s\n- 사용자의 건강 목표: %s",
                allergies.isEmpty() ? "없음" : String.join(", ", allergies),
                purposes.isEmpty() ? "없음" : String.join(", ", purposes));

        return "당신은 사용자의 건강 정보를 바탕으로 간식을 추천해주는 친절한 영양사 챗봇입니다."
                + userInfo
                + "\n\n# 사용자 질문:\n" + userMessage
                + "\n\n# 답변:\n위 정보를 바탕으로 질문에 대해 핵심만 요약하여 2~3문장으로 간결하게 답변해주세요.";
    }
}