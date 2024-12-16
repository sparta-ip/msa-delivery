package com.msa_delivery.user.infrastructure.client;

import com.msa_delivery.user.application.dtos.ApiResponseDto;
import com.msa_delivery.user.application.service.HubService;
import com.msa_delivery.user.infrastructure.dtos.GetUUIDDto;
import jakarta.validation.constraints.NotBlank;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "hub-service")
public interface HubClient extends HubService {
    @GetMapping("/api/hubs/hubs")
    ResponseEntity<ApiResponseDto<GetUUIDDto>> getHubByUserId(@RequestParam(name = "hub_manager_id") Long userId,
                                                              @RequestHeader(value = "X-User_Id", required = true) @NotBlank String headerUserId,
                                                              @RequestHeader(value = "X-Username", required = true) @NotBlank String username,
                                                              @RequestHeader(value = "X-Role", required = true) @NotBlank String role);

    @DeleteMapping("/api/hubs/hubs/{hub_id}")
    ResponseEntity<ApiResponseDto<?>> softDeleteHub(@PathVariable(name = "hub_id") UUID hubId);
}
