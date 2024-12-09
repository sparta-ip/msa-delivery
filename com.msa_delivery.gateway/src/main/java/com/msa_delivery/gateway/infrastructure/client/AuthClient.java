package com.msa_delivery.gateway.infrastructure.client;

import com.msa_delivery.gateway.application.service.AuthService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "auth-service")
public interface AuthClient extends AuthService {
    @GetMapping("/auth/verify")
        // 유저 검증 API
    Boolean verifyUser(@RequestParam(value = "user_id") String username);
}
