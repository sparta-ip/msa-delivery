package com.msa_delivery.auth.application.service;

import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtProvider {

    private static final String KEY_ALGORITHM = "HmacSHA256";
    public static final String CLAIM_USER_ID  = "userId";
    public static final String CLAIM_USERNAME = "username";
    public static final String CLAIM_ROLE     = "role";

    @Value("${spring.application.name}")
    private String issuer;

    @Value("${service.jwt.secret-key}")
    private String secret;

    @Value("${service.jwt.access-expiration}")
    private Long expirationMillis;

    @Value("${security.master-key}")
    private String masterKey;

    private SecretKey signingKey;

    @PostConstruct
    public void init() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        this.signingKey = new SecretKeySpec(keyBytes, KEY_ALGORITHM);
    }

    public String getMasterKeyInHash() {
        return masterKey;
    }


    public String generateToken(Long userId, String username, String role) {
        return Jwts.builder()
                .claim(CLAIM_USER_ID, userId)
                .claim(CLAIM_USERNAME, username)
                .claim(CLAIM_ROLE, role)
                .issuer(issuer)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(signingKey)
                .compact();
    }
}
