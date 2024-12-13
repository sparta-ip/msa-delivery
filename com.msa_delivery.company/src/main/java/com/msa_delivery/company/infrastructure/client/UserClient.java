package com.msa_delivery.company.infrastructure.client;

import com.msa_delivery.company.application.dto.ApiResponseDto;
import com.msa_delivery.company.application.dto.CommonResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/api/users/{userId}")
    ResponseEntity<ApiResponseDto<UserDto>> getUserById(
            @PathVariable("userId") Long userId,
            @RequestHeader("X-User_Id") String headerUserId,
            @RequestHeader("X-Username") String username,
            @RequestHeader("X-Role") String role
    );
}