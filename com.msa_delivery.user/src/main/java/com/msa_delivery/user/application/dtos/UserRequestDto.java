package com.msa_delivery.user.application.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.msa_delivery.user.domain.entity.UserRoleEnum;
import lombok.*;


@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserRequestDto {

    private String username;
    private String password;
    private UserRoleEnum role;

    @JsonProperty("slack_id")
    private String slackId;
}
