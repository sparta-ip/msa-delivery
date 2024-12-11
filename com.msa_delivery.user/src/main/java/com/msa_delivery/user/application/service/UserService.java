package com.msa_delivery.user.application.service;

import com.msa_delivery.user.application.dtos.UserDetailResponseDto;
import com.msa_delivery.user.application.dtos.UserRequestDto;
import com.msa_delivery.user.application.dtos.UserResponseDto;
import com.msa_delivery.user.application.dtos.UserSearchDto;
import com.msa_delivery.user.domain.entity.User;
import com.msa_delivery.user.domain.entity.UserRoleEnum;
import com.msa_delivery.user.domain.repository.UserRepository;
import com.msa_delivery.user.infrastructure.dtos.VerifyUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthService authService;

    @Transactional(readOnly = true)
    public Page<UserDetailResponseDto> searchUsers(UserSearchDto userSearchDto, String userId, String userRole) {
        Long longUserId = Long.valueOf(userId);
        return userRepository.searchUsers(userSearchDto, longUserId, userRole);
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUser(String pathVariableUserId, String headerUserId, String role) {
        if (!role.equals(UserRoleEnum.MASTER.toString()) && !pathVariableUserId.equals(headerUserId)) {
            throw new IllegalArgumentException("Cannot look up other people's information.");
        }

        Long longPathVariableUserId = Long.valueOf(pathVariableUserId);
        Long longHeaderUserId = Long.valueOf(headerUserId);

        User user = role.equals(UserRoleEnum.MASTER.toString()) ?
                userRepository.findById(longPathVariableUserId).orElseThrow(()
                        -> new IllegalArgumentException("user not exist"))
                : userRepository.findById(longHeaderUserId).orElseThrow(()
                -> new IllegalArgumentException("user not exist"));
        return UserResponseDto.fromEntity(user);
    }

    // dto의 username 기반으로 정보 변경. username은 변경 불가.
    @Transactional
    public UserResponseDto updateUser(UserRequestDto userRequestDto, String pathVariableUsername, String userId, String username, String role) {
        if (!role.equals(UserRoleEnum.MASTER.toString())) {
            throw new IllegalArgumentException("appropriate role required.");
        }

        Boolean verifiedUser = authService.verifyUser(VerifyUserDto.builder()
                .userId(userId)
                .username(username)
                .role(role)
                .build());

        if (!verifiedUser) {
            throw new IllegalArgumentException("invalid user.");
        }

        /**
         * TODO : userId를 배송에서 delivery_manager_id or receiver_id or receiver_slack_id로 검색 후 OUT_FOR_DELIVERY 이외의 값이 하나라도 있을 경우 수정 불가 로직 필요.
         */

        User user = userRepository.findByUsername(pathVariableUsername).orElseThrow(()
                -> new IllegalArgumentException("user not exist."));

        if (userRequestDto.getPassword() != null && !userRequestDto.getPassword().isEmpty()) {
            String password = userRequestDto.getPassword();
            String encodedPassword = passwordEncoder.encode(password);
            return UserResponseDto.fromEntity(user.updateIfPasswordIn(userRequestDto, username, encodedPassword));
        } else {
            return UserResponseDto.fromEntity(user.update(userRequestDto, username));
        }
    }
}
