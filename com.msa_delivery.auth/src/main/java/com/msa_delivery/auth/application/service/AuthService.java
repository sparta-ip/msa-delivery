package com.msa_delivery.auth.application.service;

import com.msa_delivery.auth.application.dtos.AuthRequestDto;
import com.msa_delivery.auth.application.dtos.AuthResponseDto;
import com.msa_delivery.auth.domain.entity.User;
import com.msa_delivery.auth.domain.repository.UserRepository;
import com.msa_delivery.auth.infrastructure.dtos.VerifyUserDto;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    public AuthResponseDto signUp(AuthRequestDto userRequestDto) {

        if (userRepository.existsByUsername(userRequestDto.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        String password = userRequestDto.getPassword();
        String encodedPassword = passwordEncoder.encode(password);

        User user = User.dtoAndPasswordOf(userRequestDto, encodedPassword);
        userRepository.save(user);

        return AuthResponseDto.fromEntity(user);
    }

    public String signIn(AuthRequestDto authRequestDto) {
        User user = userRepository.findByUsername(authRequestDto.getUsername()).orElseThrow(()
                -> new IllegalArgumentException("Please check username or password"));

        if (!passwordEncoder.matches(authRequestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Please check username or password");
        }
        return createAccessToken(user);
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
            log.info("Invalid userId format: {}",  e.getMessage());
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
}
