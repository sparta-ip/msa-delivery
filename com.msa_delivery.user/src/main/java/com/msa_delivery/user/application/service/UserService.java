package com.msa_delivery.user.application.service;

import com.msa_delivery.user.application.dtos.UserDetailResponseDto;
import com.msa_delivery.user.application.dtos.UserResponseDto;
import com.msa_delivery.user.application.dtos.UserSearchDto;
import com.msa_delivery.user.domain.entity.User;
import com.msa_delivery.user.domain.entity.UserRoleEnum;
import com.msa_delivery.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // TODO : 권한에 따른 dto 적용 필요...
    public Page<UserDetailResponseDto> searchUsers(UserSearchDto userSearchDto, String userId, String userRole) {
        Long longUserId = Long.valueOf(userId);
        return userRepository.searchUsers(userSearchDto, longUserId, userRole);
    }

    public UserResponseDto getUser(String pathVariableUserId, String headerUserId, String role) {
        if (pathVariableUserId == null || headerUserId == null) {
            throw new IllegalArgumentException("User ID cannot be null.");
        }

        if (pathVariableUserId.isEmpty() || headerUserId.isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be empty.");
        }

        if (!role.equals(UserRoleEnum.MASTER.toString()) && !pathVariableUserId.equals(headerUserId)) {
            throw new IllegalArgumentException("Cannot look up other people.");
        }

        long longPathVariableUserId;
        long longHeaderUserId;
        try {
            longPathVariableUserId = Long.parseLong(pathVariableUserId);
            longHeaderUserId = Long.parseLong(headerUserId);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("not a number. please check user ID.");
        }

        User user = role.equals(UserRoleEnum.MASTER.toString()) ?
                userRepository.findById(longPathVariableUserId).orElseThrow(()
                -> new IllegalArgumentException("user not exist"))
                : userRepository.findById(longHeaderUserId).orElseThrow(()
                -> new IllegalArgumentException("user not exist"));

        return UserResponseDto.fromEntity(user);
    }
}
