package com.msa_delivery.auth.application.dtos;

import com.spring_cloud.eureka.client.auth.entity.User;
import lombok.*;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthResponseDto {
    private UUID id;
    private String email;
    private String username;
    private String role;

    public static AuthResponseDto toUserDtoFrom(User user) {
        return AuthResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .role(user.getRole().toString())
                .build();
    }
}
