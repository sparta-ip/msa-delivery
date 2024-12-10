package com.msa_delivery.company.domain.model;

import java.util.Arrays;

public enum CompanyType {
    PRODUCER,   // 셍산 업체
    RECIPIENT;   // 수령 업체

    public static CompanyType fromString(String type) {
        return Arrays.stream(CompanyType.values())
                .filter(enumValue -> enumValue.name().equalsIgnoreCase(type))   // 대소문자 구분 X
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 업체 유형이 존재하지 않습니다.: " + type));
    }
}
