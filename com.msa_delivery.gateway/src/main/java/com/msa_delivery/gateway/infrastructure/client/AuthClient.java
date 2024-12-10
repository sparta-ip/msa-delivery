package com.msa_delivery.gateway.infrastructure.client;

import com.msa_delivery.gateway.application.service.AuthService;
import com.msa_delivery.gateway.infrastructure.dtos.VerifyUserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "auth-service")
public interface AuthClient extends AuthService {
    @GetMapping("/api/auth/verify")
        // 유저 검증 API
    Boolean verifyUser(@RequestBody VerifyUserDto verifyUserDto);
}
