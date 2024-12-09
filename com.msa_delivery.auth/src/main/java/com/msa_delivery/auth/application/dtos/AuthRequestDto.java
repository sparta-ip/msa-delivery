package com.msa_delivery.auth.application.dtos;

import com.msa_delivery.auth.domain.UserRoleEnum;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthRequestDto {
    private String username;
    private String password;
    private UserRoleEnum role;
    private String slackId;
}
