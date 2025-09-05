package com.silver.domain.snack.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SnackRequestDTO {
    private List<String> healthConcerns;
    private List<String> physicalConditions;
    private List<String> dietaryRestrictions;
    private List<String> keywords;
    private List<String> allergies;
}