package com.msa_delivery.company.infrastructure.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long userId;
    private String slackId;
    // UserDto 필드 추가
}
