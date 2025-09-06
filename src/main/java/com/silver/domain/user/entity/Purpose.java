package com.silver.domain.user.entity;

public enum Purpose {
    BLOOD_SUGAR,                   // 혈당 관리 (당류↓)
    BLOOD_PRESSURE,                // 혈압 관리 (나트륨↓)
    CHOLESTEROL,                   // 콜레스테롤 관리 (포화/트랜스/콜레스테롤↓)
    WEIGHT_CONTROL,                // 체중 관리 (열량/당류/지방↓, 단백질/식이섬유↑)
    KIDNEY,                        // 신장 건강 (칼륨/나트륨↓)
    HEART                          // 심혈관 건강 (나트륨/포화/트랜스/콜레스테롤↓)
}
