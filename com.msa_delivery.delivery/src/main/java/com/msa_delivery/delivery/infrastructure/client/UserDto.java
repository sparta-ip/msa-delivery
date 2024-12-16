package com.msa_delivery.delivery.infrastructure.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    @JsonProperty("userId")
    private Long userId;
    @JsonProperty("slackId")
    private String slackId;
}
