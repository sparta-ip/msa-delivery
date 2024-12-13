package com.msa_delivery.delivery.infrastructure.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long userId;
    private String slackId;
}
