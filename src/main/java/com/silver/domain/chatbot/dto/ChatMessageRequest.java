package com.silver.domain.chatbot.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatMessageRequest {
    // 이전 대화 기록(history) 필드를 제거하고, 현재 메시지만 받도록 수정
    private String message;
}