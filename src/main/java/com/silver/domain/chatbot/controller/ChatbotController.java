package com.silver.domain.chatbot.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.silver.domain.chatbot.dto.ChatMessageRequest;
import com.silver.domain.chatbot.service.ChatbotService;
import com.silver.global.config.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> streamAiResponse(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody ChatMessageRequest request) {

        SseEmitter emitter = new SseEmitter(60_000L);

        // request 객체 대신, 내부의 message 문자열만 서비스로 전달하도록 수정
        chatbotService.streamAiResponse(userDetails, request.getMessage(), emitter);

        return ResponseEntity.ok(emitter);
    }
}