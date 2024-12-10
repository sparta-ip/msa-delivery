package com.msa_delivery.auth.application.service;

import com.msa_delivery.auth.application.dtos.AuthRequestDto;
import com.msa_delivery.auth.application.dtos.AuthResponseDto;
import com.msa_delivery.auth.domain.entity.User;
import com.msa_delivery.auth.domain.repository.UserRepository;
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

    public String createAccessToken(User user) {
        return BEARER_PREFIX + Jwts.builder()
                .claim("user_id", user.getUserId())
                .claim("email", user.getUsername())
                .claim("role", user.getRole())
                .issuer(issuer)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + accessExpiration))
                .signWith(getSigningKey())
                .compact();
    }
}
