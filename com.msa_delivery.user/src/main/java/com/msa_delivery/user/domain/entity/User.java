package com.msa_delivery.user.domain.entity;

import com.msa_delivery.user.application.dtos.UserRequestDto;
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

    public void updateByUser(String username) {
        updatedBy = username;
    }

    public User update(UserRequestDto userRequestDto, String username) {
        if (userRequestDto.getRole() != null) {
            role = userRequestDto.getRole();
        }
        if (userRequestDto.getSlackId() != null) {
            slackId = userRequestDto.getSlackId();
        }

        updateByUser(username);

        return this;
    }

    public User updateIfPasswordIn(UserRequestDto userRequestDto, String username, String password) {
        this.password = password;

        if (userRequestDto.getRole() != null) {
            role = userRequestDto.getRole();
        }
        if (userRequestDto.getSlackId() != null) {
            slackId = userRequestDto.getSlackId();
        }

        updateByUser(username);

        return this;
    }

    public void softDeleteUser(String username) {
        isDeleted = true;
        deletedAt = LocalDateTime.now();
        deletedBy = username;
        updateByUser(username);
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }
}
