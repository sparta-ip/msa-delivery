package com.msa_delivery.user.application.dtos;

import com.msa_delivery.user.domain.entity.User;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UserResponseDto {
    private String username;
    private String role;
    private String slackId;

    public static UserResponseDto fromEntity(User user) {
        return UserResponseDto.builder()
                .username(user.getUsername())
                .role(user.getRole().toString())
                .slackId(user.getSlackId())
                .build();
    }
}
