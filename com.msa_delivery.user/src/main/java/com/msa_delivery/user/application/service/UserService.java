package com.msa_delivery.user.application.service;

import com.msa_delivery.user.application.dtos.*;
import com.msa_delivery.user.domain.entity.User;
import com.msa_delivery.user.domain.entity.UserRoleEnum;
import com.msa_delivery.user.domain.repository.UserRepository;
import com.msa_delivery.user.infrastructure.dtos.VerifyUserDto;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    @PostConstruct
    public void registerEventListeners() {
        registerEventListener("searchUsersCircuitBreaker");
        registerEventListener("getUserCircuitBreaker");
        registerEventListener("updateUserCircuitBreaker");
        registerEventListener("softDeleteUserCircuitBreaker");
    }

    @CircuitBreaker(name = "searchUsersCircuitBreaker", fallbackMethod = "fallbackSearchUsers")
    @Transactional(readOnly = true)
    public ApiResponseDto<Page<UserDetailResponseDto>> searchUsers(UserSearchDto userSearchDto, String userId, String userRole) {
        Long longUserId = Long.valueOf(userId);

        return ApiResponseDto.response(HttpStatus.OK.value(),
                "조회에 성공하였습니다.",
                userRepository.searchUsers(userSearchDto, longUserId, userRole));
    }

    @CircuitBreaker(name = "getUserCircuitBreaker", fallbackMethod = "fallbackGetUsers")
    @Transactional(readOnly = true)
    public ApiResponseDto<UserResponseDto> getUser(String userId, String headerUserId, String role) {
        if (!role.equals(UserRoleEnum.MASTER.toString()) && !userId.equals(headerUserId)) {
            throw new IllegalArgumentException("Cannot look up other people's information.");
        }

        Long longPathVariableUserId = Long.valueOf(userId);
        Long longHeaderUserId = Long.valueOf(headerUserId);

        User user = role.equals(UserRoleEnum.MASTER.toString()) ?
                userRepository.findById(longPathVariableUserId).orElseThrow(()
                        -> new IllegalArgumentException("user not exist"))
                : userRepository.findById(longHeaderUserId).orElseThrow(()
                -> new IllegalArgumentException("user not exist"));

        return ApiResponseDto.response(HttpStatus.OK.value(),
                "조회에 성공하였습니다.",
                UserResponseDto.fromEntity(user));
    }

    @CircuitBreaker(name = "updateUserCircuitBreaker", fallbackMethod = "fallbackUpdateUser")
    @Retry(name = "defaultRetry")
    @Transactional
    public ApiResponseDto<UserResponseDto> updateUser(UserRequestDto userRequestDto, String username, String userId, String headerUsername, String role) {
        checkIsMaster(role);
        verifyUserToAuth(userId, headerUsername, role);

        User user = userRepository.findByUsername(username).orElseThrow(()
                -> new IllegalArgumentException("user not exist."));

        if (userRequestDto.getPassword() != null && !userRequestDto.getPassword().isEmpty()) {
            String password = userRequestDto.getPassword();
            String encodedPassword = passwordEncoder.encode(password);
            return ApiResponseDto.response(HttpStatus.OK.value(),
                    "유저 정보 수정에 성공하였습니다.",
                    UserResponseDto.fromEntity(user.updateIfPasswordIn(userRequestDto, headerUsername, encodedPassword)));
        } else {
            return ApiResponseDto.response(HttpStatus.OK.value(),
                    "유저 정보 수정에 성공하였습니다.",
                    UserResponseDto.fromEntity(user.update(userRequestDto, headerUsername)));
        }
    }

    @CircuitBreaker(name = "softDeleteUserCircuitBreaker", fallbackMethod = "fallbackSoftDeleteUser")
    @Retry(name = "defaultRetry")
    @Transactional
    public ApiResponseDto<?> softDeleteUser(String username, String userId, String headerUsername, String role) {
        checkIsMaster(role);
        verifyUserToAuth(userId, headerUsername, role);
        /**
         * TODO : userId를 배송에서 delivery_manager_id or receiver_id or receiver_slack_id로 검색 후 OUT_FOR_DELIVERY 이외의 값이 하나라도 있을 경우 삭제 불가 로직 필요.
         */
        User user = userRepository.findByUsername(username).orElseThrow(()
                -> new IllegalArgumentException("user not exist."));
        user.softDeleteUser(headerUsername);
        return ApiResponseDto.response(HttpStatus.OK.value(),
                "해당 유저 삭제에 성공하였습니다.",
                "");
    }

    public ApiResponseDto<Page<UserDetailResponseDto>> fallbackSearchUsers(UserSearchDto userSearchDto, String userId, String userRole, Throwable throwable) {
        HttpStatus status = throwable instanceof CallNotPermittedException
                ? HttpStatus.SERVICE_UNAVAILABLE
                : HttpStatus.BAD_REQUEST;
        return ApiResponseDto.response(status.value(), throwable.getMessage(), null);
    }

    public ApiResponseDto<UserResponseDto> fallbackGetUsers(String userId, String headerUserId, String role, Throwable throwable) {
        HttpStatus status = throwable instanceof CallNotPermittedException
                ? HttpStatus.SERVICE_UNAVAILABLE
                : HttpStatus.BAD_REQUEST;
        return ApiResponseDto.response(status.value(), throwable.getMessage(), null);
    }

    public ApiResponseDto<UserResponseDto> fallbackUpdateUser(UserRequestDto userRequestDto, String username, String userId, String headerUsername, String role, Throwable throwable) {
        HttpStatus status = throwable instanceof CallNotPermittedException
                ? HttpStatus.SERVICE_UNAVAILABLE
                : HttpStatus.BAD_REQUEST;
        return ApiResponseDto.response(status.value(), throwable.getMessage(), null);
    }

    public ApiResponseDto<?> fallbackSoftDeleteUser(String username, String userId, String headerUsername, String role, Throwable throwable) {
        HttpStatus status = throwable instanceof CallNotPermittedException
                ? HttpStatus.SERVICE_UNAVAILABLE
                : HttpStatus.BAD_REQUEST;
        return ApiResponseDto.response(status.value(), throwable.getMessage(), null);
    }

    public void registerEventListener(String circuitBreakerName) {
        circuitBreakerRegistry.circuitBreaker(circuitBreakerName).getEventPublisher()
                .onStateTransition(event -> log.info("###CircuitBreaker State Transition: {}", event)) // 상태 전환 이벤트 리스너
                .onFailureRateExceeded(event -> log.info("###CircuitBreaker Failure Rate Exceeded: {}", event)) // 실패율 초과 이벤트 리스너
                .onCallNotPermitted(event -> log.info("###CircuitBreaker Call Not Permitted: {}", event)) // 호출 차단 이벤트 리스너
                .onError(event -> log.info("###CircuitBreaker Error: {}", event)); // 오류 발생 이벤트 리스너
    }

    private void checkIsMaster(String role) {
        if (!role.equals(UserRoleEnum.MASTER.toString())) {
            throw new IllegalArgumentException("appropriate role required.");
        }
    }

    private void verifyUserToAuth(String userId, String username, String role) {
        Boolean verifiedUser = authService.verifyUser(VerifyUserDto.builder()
                .userId(userId)
                .username(username)
                .role(role)
                .build());

        if (!verifiedUser) {
            throw new IllegalArgumentException("invalid user.");
        }
    }
}