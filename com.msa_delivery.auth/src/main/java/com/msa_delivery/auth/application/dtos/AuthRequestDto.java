package com.msa_delivery.auth.application.dtos;

import com.spring_cloud.eureka.client.auth.entity.UserRoleEnum;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthRequestDto {
    private String email;
    private String username;
    private String password;
    private UserRoleEnum role;
}
