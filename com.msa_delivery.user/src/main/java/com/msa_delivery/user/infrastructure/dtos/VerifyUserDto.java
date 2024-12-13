package com.msa_delivery.user.infrastructure.dtos;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class VerifyUserDto {
    private String userId;
    private String username;
    private String role;
}
