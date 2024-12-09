package com.msa_delivery.auth.application.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Value("${service.jwt.secret-key}")
    private String secretKey;

    @Value("${service.jwt.access-expiration}")
    private Long accessExpiration;

    private static final String BEARER_PREFIX = "Bearer ";
}
