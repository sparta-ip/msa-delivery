package com.msa_delivery.user.application.service;

import com.msa_delivery.user.application.UserSearchDto;
import com.msa_delivery.user.application.dtos.UserDetailResponseDto;
import com.msa_delivery.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Page<UserDetailResponseDto> searchUsers(UserSearchDto userSearchDto, String userId, String userRole) {
        Long longUserId = Long.valueOf(userId);
        return userRepository.searchUsers(userSearchDto, longUserId, userRole);
    }
}
