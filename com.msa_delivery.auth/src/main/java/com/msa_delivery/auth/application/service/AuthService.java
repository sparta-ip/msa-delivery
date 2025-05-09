package com.msa_delivery.auth.application.service;

import com.msa_delivery.auth.application.dtos.ApiResponseDto;
import com.msa_delivery.auth.application.dtos.AuthRequestDto;
import com.msa_delivery.auth.application.dtos.AuthResponseDto;
import com.msa_delivery.auth.domain.entity.User;
import com.msa_delivery.auth.domain.entity.UserRoleEnum;
import com.msa_delivery.auth.domain.repository.UserRepository;
import com.msa_delivery.auth.infrastructure.dtos.VerifyUserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    private static final String MSG_SIGNUP_SUCCESS      = "Sign up completed successfully.";
    private static final String MSG_LOGIN_SUCCESS       = "Sign in completed successfully.";
    private static final String ERR_USERNAME_EXISTS     = "Username already exists";
    private static final String ERR_INVALID_CREDENTIALS = "Please check username or password";

    public ResponseEntity<ApiResponseDto<AuthResponseDto>> signUp(AuthRequestDto authRequestDto) {
        try {
            validateSignUp(authRequestDto);
            String encodedPassword = passwordEncoder.encode(authRequestDto.getPassword());
            User user = User.dtoAndPasswordOf(authRequestDto, encodedPassword);
            userRepository.save(user);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponseDto.response(
                            HttpStatus.CREATED.value(),
                            MSG_SIGNUP_SUCCESS,
                            AuthResponseDto.from(user)
                    ));
        } catch (IllegalArgumentException e) {
            log.warn(
                    "Sign up failed for user: {}, reason: {}",
                    authRequestDto.getUsername(),
                    e.getMessage()
            );
            throw e;
        } catch (Exception e) {
            log.error(
                    "Unexpected error during sign up for user: {}",
                    authRequestDto.getUsername(),
                    e
            );
            throw e;
        }
    }

    public ResponseEntity<ApiResponseDto<Void>> signIn(AuthRequestDto authRequestDto) {
        try {
            User user = userRepository.findByUsername(authRequestDto.getUsername())
                    .orElseGet(() -> {
                        // TODO : 반복되는 로깅이나 예외처리는 AOP를 통해 간단하게 유지보수 할 수 있을 것 같다.
                        log.warn(
                                "Sign in failed for user: {}, reason: {}",
                                authRequestDto.getUsername(),
                                ERR_INVALID_CREDENTIALS
                        );
                        throw new IllegalArgumentException(ERR_INVALID_CREDENTIALS);
                    });

            if (!passwordEncoder.matches(authRequestDto.getPassword(), user.getPassword())) {
                log.warn(
                        "Sign in failed for user: {}, reason: {}",
                        authRequestDto.getUsername(),
                        ERR_INVALID_CREDENTIALS
                );
                throw new IllegalArgumentException(ERR_INVALID_CREDENTIALS);
            }

            String token = jwtProvider.generateToken(
                    user.getUserId(),
                    user.getUsername(),
                    user.getRole().name()
            );

            return ResponseEntity.ok()
                    .header("Authorization", "Bearer " + token)
                    .body(ApiResponseDto.response(
                            HttpStatus.OK.value(),
                            MSG_LOGIN_SUCCESS,
                            null
                    ));
        } catch (IllegalArgumentException e) {
            log.warn(
                    "Sign in failed for user: {}, reason: {}",
                    authRequestDto.getUsername(),
                    e.getMessage()
            );
            throw e;
        } catch (Exception e) {
            log.error(
                    "Unexpected error during sign in for user: {}",
                    authRequestDto.getUsername(),
                    e
            );
            throw e;
        }
    }

    public Boolean verifyUser(VerifyUserDto verifyUserDto) {
        try {
            long userId = Long.parseLong(verifyUserDto.getUserId());
            User user = userRepository.findById(userId)
                    .orElseGet(() -> {
                        log.info(
                                "verifyUser failed - user not found with id: {}",
                                verifyUserDto.getUserId()
                        );
                        throw new IllegalArgumentException(
                                "User not found with id: " + verifyUserDto.getUserId()
                        );
                    });

            boolean valid = user.getUsername().equals(verifyUserDto.getUsername())
                    && user.getRole().name().equals(verifyUserDto.getRole());
            if (!valid) {
                log.info(
                        "verifyUser failed - credentials mismatch for userId: {}",
                        verifyUserDto.getUserId()
                );
            }
            return valid;
        } catch (NumberFormatException e) {
            log.warn(
                    "verifyUser failed - invalid userId format: {}",
                    verifyUserDto.getUserId()
            );
            return false;
        } catch (IllegalArgumentException e) {
            log.info(
                    "verifyUser failed: {}",
                    e.getMessage()
            );
            return false;
        } catch (Exception e) {
            log.error(
                    "Unexpected error during verifyUser: {}",
                    e.getMessage(), e
            );
            return false;
        }
    }

    private void validateSignUp(AuthRequestDto authRequestDto) {
        if (userRepository.existsByUsername(authRequestDto.getUsername())) {
            log.warn(
                    "Sign up validation failed - username already exists: {}",
                    authRequestDto.getUsername()
            );
            throw new IllegalArgumentException(ERR_USERNAME_EXISTS);
        }
        if (authRequestDto.getRole() == UserRoleEnum.MASTER &&
                (authRequestDto.getMasterKey() == null ||
                        !authRequestDto.getMasterKey().equals(getMasterKeyHash()))) {
            log.warn(
                    "Sign up validation failed - invalid master key for user: {}",
                    authRequestDto.getUsername()
            );
            throw new IllegalArgumentException("Invalid master key");
        }
    }

    private String getMasterKeyHash() {
        return jwtProvider.getMasterKeyInHash();
    }
}