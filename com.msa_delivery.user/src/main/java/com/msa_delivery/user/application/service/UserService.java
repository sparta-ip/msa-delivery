package com.msa_delivery.user.application.service;

import com.msa_delivery.user.application.dtos.*;
import com.msa_delivery.user.domain.entity.User;
import com.msa_delivery.user.domain.entity.UserRoleEnum;
import com.msa_delivery.user.domain.repository.UserRepository;
import com.msa_delivery.user.infrastructure.dtos.GetUUIDDto;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final CompanyService companyService;
    private final DeliveryService deliveryService;
    private final HubService hubService;
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
    public ResponseEntity<ApiResponseDto<Page<? extends UserDetailResponseDto>>> searchUsers(UserSearchDto userSearchDto, String userId, String userRole) {
        Long longUserId = Long.valueOf(userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponseDto.response(HttpStatus.OK.value(),
                        "조회에 성공하였습니다.",
                        userRepository.searchUsers(userSearchDto, longUserId, userRole)));
    }

    @CircuitBreaker(name = "getUserCircuitBreaker", fallbackMethod = "fallbackGetUsers")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponseDto<? extends UserResponseDto>> getUser(Long userId, String headerUserId, String role) {
        Long longHeaderUserId = Long.valueOf(headerUserId);

        if (!role.equals(UserRoleEnum.MASTER.toString()) && userId.equals(longHeaderUserId)) {
            throw new IllegalArgumentException("Cannot look up other people's information.");
        }

        User user = role.equals(UserRoleEnum.MASTER.toString()) ?
                userRepository.findById(userId).orElseThrow(()
                        -> new IllegalArgumentException("user not exist"))
                : userRepository.findById(longHeaderUserId).orElseThrow(()
                -> new IllegalArgumentException("user not exist"));

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponseDto.response(HttpStatus.OK.value(),
                        "조회에 성공하였습니다.",
                        UserResponseDto.from(user)));
    }

    @CircuitBreaker(name = "updateUserCircuitBreaker", fallbackMethod = "fallbackUpdateUser")
    @Retry(name = "defaultRetry")
    @Transactional
    public ResponseEntity<ApiResponseDto<? extends UserResponseDto>> updateUser(UserRequestDto userRequestDto, String username, String userId, String headerUsername, String role) {
        checkIsMaster(role);
        verifyUserToAuth(userId, headerUsername, role);

        User user = userRepository.findByUsername(username).orElseThrow(()
                -> new IllegalArgumentException("user not exist."));

        if (userRequestDto.getPassword() != null && !userRequestDto.getPassword().isEmpty()) {
            String password = userRequestDto.getPassword();
            String encodedPassword = passwordEncoder.encode(password);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponseDto.response(HttpStatus.OK.value(),
                            "유저 정보 수정에 성공하였습니다.",
                            UserResponseDto.from(user.updateIfPasswordIn(userRequestDto, headerUsername, encodedPassword))));
        } else {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponseDto.response(HttpStatus.OK.value(),
                            "유저 정보 수정에 성공하였습니다.",
                            UserResponseDto.from(user.update(userRequestDto, headerUsername))));
        }
    }

    @CircuitBreaker(name = "softDeleteUserCircuitBreaker", fallbackMethod = "fallbackSoftDeleteUser")
    @Retry(name = "defaultRetry")
    @Transactional
    public ResponseEntity<ApiResponseDto<? extends UserResponseDto>> softDeleteUser(String username, String headerUserId, String headerUsername, String role) {
        checkIsMaster(role);
        verifyUserToAuth(headerUserId, headerUsername, role);
        /**
         * TODO : userId를 각 서버에 검색으로 이용해 delivery_manager_id, hub_id, company_id를 받아와 삭제 진행.
         * TODO : SAGA 패턴을 적용하려면 비동기 및 보상 로직이 필요하니, 이번 프로젝트는 시간이 걸리지만 동기 형식으로 진행
         */
        User user = userRepository.findByUsername(username).orElseThrow(()
                -> new IllegalArgumentException("user not exist."));

        // delivery 삭제 요청

        // delivery manager 삭제 요청
        ResponseEntity<ApiResponseDto<GetUUIDDto>> deliveryManagerByUserId = deliveryService.getDeliveryManagerByUserId(user.getUserId(), headerUserId, headerUsername, role);
        ApiResponseDto<GetUUIDDto> deliveryManagerBody = deliveryManagerByUserId.getBody();

        if (Objects.requireNonNull(deliveryManagerBody).getStatus() == HttpStatus.OK.value() ||
                (deliveryManagerBody.getData() != null &&
                        deliveryManagerBody.getData().getContent() != null &&
                        !deliveryManagerBody.getData().getContent().isEmpty())) {

            List<GetUUIDDto.UUIDListDto> contentList = deliveryManagerBody.getData().getContent();

            for (GetUUIDDto.UUIDListDto content : contentList) {
                if (content != null && content.getDeliveryManagerId() != null) {
                    deliveryService.softDeleteDeliveryManager(content.getDeliveryManagerId(), headerUserId, headerUsername, role);
                }
            }
        }
        // company 삭제 요청

        // hub 삭제 요청

        user.softDeleteUser(headerUsername);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponseDto.response(HttpStatus.OK.value(),
                        "해당 유저 삭제에 성공하였습니다.",
                        null));
    }

    public ResponseEntity<ApiResponseDto<Page<? extends UserDetailResponseDto>>> fallbackSearchUsers(UserSearchDto userSearchDto, String userId, String userRole, Throwable throwable) {
        HttpStatus status = throwable instanceof CallNotPermittedException
                ? HttpStatus.SERVICE_UNAVAILABLE
                : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status)
                .body(ApiResponseDto.response(status.value(), throwable.getMessage(), null));
    }

    public ResponseEntity<ApiResponseDto<? extends UserResponseDto>> fallbackGetUsers(Long userId, String headerUserId, String role, Throwable throwable) {
        HttpStatus status = throwable instanceof CallNotPermittedException
                ? HttpStatus.SERVICE_UNAVAILABLE
                : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status)
                .body(ApiResponseDto.response(status.value(), throwable.getMessage(), null));
    }

    public ResponseEntity<ApiResponseDto<? extends UserResponseDto>> fallbackUpdateUser(UserRequestDto userRequestDto, String username, String userId, String headerUsername, String role, Throwable throwable) {
        HttpStatus status = throwable instanceof CallNotPermittedException
                ? HttpStatus.SERVICE_UNAVAILABLE
                : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status)
                .body(ApiResponseDto.response(status.value(), throwable.getMessage(), null));
    }

    public ResponseEntity<ApiResponseDto<? extends UserResponseDto>> fallbackSoftDeleteUser(String username, String userId, String headerUsername, String role, Throwable throwable) {
        HttpStatus status = throwable instanceof CallNotPermittedException
                ? HttpStatus.SERVICE_UNAVAILABLE
                : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status)
                .body(ApiResponseDto.response(status.value(), throwable.getMessage(), null));
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
