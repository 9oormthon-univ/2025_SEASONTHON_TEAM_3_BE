package com.silver.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter

public class UserInfoResponseDto {
    private String username;
    private String email;
    private Set<String> allergies;
    private Set<String> purposes;
    private LocalDateTime createdAt;
}