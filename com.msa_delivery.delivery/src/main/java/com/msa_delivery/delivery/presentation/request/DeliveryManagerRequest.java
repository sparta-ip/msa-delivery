package com.msa_delivery.delivery.presentation.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class DeliveryManagerRequest {

    @NotNull(message = "유저 ID는 필수 입력 값입니다.")
    private Long userId;

    @NotBlank(message = "배송 담당자 타입은 필수 입력 값입니다. ('HUB_DELIVERY_MANAGER' 또는 'COMPANY_DELIVERY_MANAGER')")
    private String type;

    private UUID hubId;
}
