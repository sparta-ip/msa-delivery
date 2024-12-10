package com.msa_delivery.company.presentation.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CompanyRequest {

    @NotNull(message = "담당자 ID는 필수 입력 값입니다.")
    private Long managerId;

    @NotBlank(message = "업체 이름은 필수 입력 값입니다.")
    private String name;

    @NotBlank(message = "주소는 필수 입력 값입니다.")
    private String address;

    // Type은 enum 변환 로직으로 처리
    @NotBlank(message = "업체 유형은 필수 입력 값입니다. ('PRODUCER' 또는 'RECIPIENT')")
    private String type;
}
