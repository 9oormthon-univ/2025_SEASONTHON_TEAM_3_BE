package com.silver.domain.user.dto.request;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpRequestDto {
    private String email;
    private String password;
    private String name;
    private String purpose;
    private String allergy;
    private LocalDateTime createdAt;

}
