package com.silver.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter

public class UserInfoResponseDto {

    private String username;
    private String email;
    private String purpose;
    private String allergy;
    private LocalDateTime createdAt;
}