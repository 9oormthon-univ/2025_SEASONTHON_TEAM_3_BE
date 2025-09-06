package com.silver.domain.user.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.util.Set;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateProfileRequestDto {
    private String name;
    private String email;
    private Set<String> allergies;
    private Set<String> purposes;
}
