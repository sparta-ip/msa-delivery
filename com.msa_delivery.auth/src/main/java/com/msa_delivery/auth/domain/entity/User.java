package com.msa_delivery.auth.domain.entity;

import com.msa_delivery.auth.application.dtos.AuthRequestDto;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Table(name = "p_users", schema = "MEMBER")
@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(length = 50, nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRoleEnum role;

    @Column(length = 50, nullable = false, name = "slack_id")
    private String slackId;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(length = 50, name = "created_by", nullable = false, updatable = false)
    private String createdBy;

    @LastModifiedDate
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updatedAt;

    @Column(length = 100, name = "updated_by")
    private String updatedBy;

    @Column(name = "deleted_at")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime deletedAt;

    @Column(length = 100, name = "deleted_by")
    private String deletedBy;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    public static User dtoAndPasswordOf(final AuthRequestDto authRequestDto, final String password) {
        return User.builder()
                .username(authRequestDto.getUsername())
                .password(password)
                .role(authRequestDto.getRole())
                .slackId(authRequestDto.getSlackId())
                .createdBy("SYSTEM")
                .updatedBy("SYSTEM")
                .build();
    }
}
