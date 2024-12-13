package com.msa_delivery.company.infrastructure.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    @JsonProperty("userId")
    private Long userId;
    @JsonProperty("slackId")
    private String slackId;
}
