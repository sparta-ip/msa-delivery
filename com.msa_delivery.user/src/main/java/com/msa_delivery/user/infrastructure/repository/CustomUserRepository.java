package com.msa_delivery.user.infrastructure.repository;

import com.msa_delivery.user.application.UserSearchDto;
import com.msa_delivery.user.application.dtos.UserDetailResponseDto;
import org.springframework.data.domain.Page;

public interface CustomUserRepository {
    Page<UserDetailResponseDto> searchUsers(UserSearchDto searchDto, Long userId, String role);
}
