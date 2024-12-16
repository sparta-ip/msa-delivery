package com.msa_delivery.user.infrastructure.client;

import com.msa_delivery.user.application.dtos.ApiResponseDto;
import com.msa_delivery.user.application.service.DeliveryService;
import com.msa_delivery.user.infrastructure.dtos.GetUUIDDto;
import jakarta.validation.constraints.NotBlank;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "delivery-service")
public interface DeliveryClient extends DeliveryService {
    @GetMapping("/api/deliveries/deliveries")
    ResponseEntity<ApiResponseDto<GetUUIDDto>> getDeliveryByUserId(@RequestParam(name = "delivery_manager_id") Long userId,
                                                                   @RequestHeader(value = "X-User_Id", required = true) @NotBlank String headerUserId,
                                                                   @RequestHeader(value = "X-Username", required = true) @NotBlank String username,
                                                                   @RequestHeader(value = "X-Role", required = true) @NotBlank String role);

    @DeleteMapping("/api/deliveries/deliveries/{delivery_id}")
    ResponseEntity<ApiResponseDto<?>> softDeleteDelivery(@PathVariable(name = "delivery_id") UUID deliveryId);
}
