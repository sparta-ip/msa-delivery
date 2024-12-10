package com.msa_delivery.company.infrastructure.client;

import com.msa_delivery.company.application.dto.CommonResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/api/users/{id}")
    ResponseEntity<CommonResponse<UserDto>> getUserById(@PathVariable Long id);
}
