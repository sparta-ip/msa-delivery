package com.msa_delivery.auth.domain;

import com.msa_delivery.auth.application.dtos.AuthRequestDto;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "p_users", schema = "MEMBER")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(unique = true)
    private String username;
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRoleEnum role;

    @Column(name = "slack_id")
    private String slackId;

    public static User DtoAndPasswordOf(AuthRequestDto authRequestDto, String password) {
        return User.builder()
                .username(authRequestDto.getUsername())
                .password(password)
                .role(authRequestDto.getRole())
                .slackId(authRequestDto.getSlackId())
                .build();
    }
}
