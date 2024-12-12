package com.msa_delivery.auth.application.service;

import com.msa_delivery.auth.application.dtos.ApiResponseDto;
import com.msa_delivery.auth.application.dtos.AuthRequestDto;
import com.msa_delivery.auth.application.dtos.AuthResponseDto;
import com.msa_delivery.auth.domain.entity.User;
import com.msa_delivery.auth.domain.repository.UserRepository;
import com.msa_delivery.auth.infrastructure.dtos.VerifyUserDto;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    @Value("${spring.application.name}")
    private String issuer;

    @Value("${service.jwt.secret-key}")
    private String secretKey;

    @Value("${service.jwt.access-expiration}")
    private Long accessExpiration;

    private static final String KEY_ALGORITHM = "HmacSHA256";
    private static final String BEARER_PREFIX = "Bearer ";

    private SecretKey getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(keyBytes, KEY_ALGORITHM);
    }

    @CircuitBreaker(name = "signUpCircuitBreaker", fallbackMethod = "fallbackSignUp")
    @Retry(name = "defaultRetry", fallbackMethod = "fallbackSignUp")
    public ApiResponseDto<AuthResponseDto> signUp(AuthRequestDto userRequestDto) {
        if (userRepository.existsByUsername(userRequestDto.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        String password = userRequestDto.getPassword();
        String encodedPassword = passwordEncoder.encode(password);

        User user = User.dtoAndPasswordOf(userRequestDto, encodedPassword);
        userRepository.save(user);

        return ApiResponseDto.response(HttpStatus.CREATED.value(),
                "회원가입이 완료되었습니다.",
                AuthResponseDto.fromEntity(user));
    }

    @CircuitBreaker(name = "signInCircuitBreaker", fallbackMethod = "fallbackSignIn")
    @Retry(name = "defaultRetry", fallbackMethod = "fallbackSignIn")
    public ResponseEntity<ApiResponseDto<?>> signIn(AuthRequestDto authRequestDto) {
        User user = userRepository.findByUsername(authRequestDto.getUsername()).orElseThrow(()
                -> new IllegalArgumentException("Please check username or password"));

        if (!passwordEncoder.matches(authRequestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Please check username or password");
        }

        return ResponseEntity.status(HttpStatus.OK.value())
                .header("Authorization", createAccessToken(user))
                .body(ApiResponseDto.response(200,
                        "로그인에 성공하였습니다.",
                        ""));
    }

    public Boolean verifyUser(VerifyUserDto verifyUserDto) {
        try {
            Long longUserId = Long.valueOf(verifyUserDto.getUserId());
            User user = userRepository.findById(longUserId).orElseThrow(()
                    -> new IllegalArgumentException("Please check your id"));

            if (!user.getUsername().equals(verifyUserDto.getUsername()) || !user.getRole().toString().equals(verifyUserDto.getRole())) {
                return false;
            }
        } catch (NumberFormatException e) {
            log.info("Invalid userId format: {}", e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            log.info(e.getMessage());
            return false;
        } catch (Exception e) {
            log.info("Unexpected error: {}", e.getMessage());
            return false;
        }

        return true;
    }

    public String createAccessToken(User user) {
        return BEARER_PREFIX + Jwts.builder()
                .claim("userId", user.getUserId())
                .claim("username", user.getUsername())
                .claim("role", user.getRole())
                .issuer(issuer)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + accessExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    public ApiResponseDto<AuthResponseDto> fallbackSignUp(AuthRequestDto authRequestDto, Throwable throwable) {
        HttpStatus status = throwable instanceof CallNotPermittedException
                ? HttpStatus.SERVICE_UNAVAILABLE
                : HttpStatus.BAD_REQUEST;

        return ApiResponseDto.response(status.value(), throwable.getMessage(), null);
    }

    public ResponseEntity<ApiResponseDto<?>> fallbackSignIn(AuthRequestDto authRequestDto, Throwable throwable) {
        HttpStatus status = throwable instanceof CallNotPermittedException
                ? HttpStatus.SERVICE_UNAVAILABLE
                : HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status)
                .body(ApiResponseDto.response(status.value(),
                        throwable.getMessage(),
                        ""));
    }

    @PostConstruct
    public void registerEventListeners() {
        registerEventListener("signUpCircuitBreaker");
        registerEventListener("signInCircuitBreaker");
    }

    public void registerEventListener(String circuitBreakerName) {
        circuitBreakerRegistry.circuitBreaker(circuitBreakerName).getEventPublisher()
                .onStateTransition(event -> log.info("###CircuitBreaker State Transition: {}", event)) // 상태 전환 이벤트 리스너
                .onFailureRateExceeded(event -> log.info("###CircuitBreaker Failure Rate Exceeded: {}", event)) // 실패율 초과 이벤트 리스너
                .onCallNotPermitted(event -> log.info("###CircuitBreaker Call Not Permitted: {}", event)) // 호출 차단 이벤트 리스너
                .onError(event -> log.info("###CircuitBreaker Error: {}", event)); // 오류 발생 이벤트 리스너
    }
}
