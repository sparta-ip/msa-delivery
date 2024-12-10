package com.msa_delivery.company.infrastructure.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HubDto {
    private UUID hubId;
    // HubDto 필드 추가
}
