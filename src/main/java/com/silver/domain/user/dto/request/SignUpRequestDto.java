package com.silver.domain.user.dto.request;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpRequestDto {
    private String email;
    private String password;
    private String name;
    private Set<String> allergies;
    private Set<String> purposes;
    private LocalDateTime createdAt;
}
