package com.msa_delivery.auth.application.dtos;

import com.msa_delivery.auth.domain.User;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthResponseDto {
    private String username;
    private String role;
    private String slackId;

    public static AuthResponseDto fromEntity(User user) {
        return AuthResponseDto.builder()
                .username(user.getUsername())
                .role(user.getRole().toString())
                .slackId(user.getSlackId())
                .build();
    }
}
