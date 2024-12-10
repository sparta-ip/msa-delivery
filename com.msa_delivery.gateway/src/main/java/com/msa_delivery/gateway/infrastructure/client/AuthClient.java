package com.msa_delivery.gateway.infrastructure.client;

import com.msa_delivery.gateway.application.service.AuthService;
import com.msa_delivery.gateway.infrastructure.dtos.VerifyUserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "auth-service")
public interface AuthClient extends AuthService {
    @PostMapping("/api/auth/verify")
    Boolean verifyUser(@RequestBody VerifyUserDto verifyUserDto);
}
