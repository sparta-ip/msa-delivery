package com.msa_delivery.user.application;

import com.msa_delivery.user.domain.entity.UserRoleEnum;
import lombok.*;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSearchDto {

    private String username;
    private UserRoleEnum role;
    private String slackId;
    private LocalDateTime createdAtStart;
    private LocalDateTime createdAtEnd;
    private LocalDateTime updatedAtStart;
    private LocalDateTime updatedAtEnd;
    private boolean isDeleted;

    private int page = 1;
    private int size;
    private Sort sort;
}
