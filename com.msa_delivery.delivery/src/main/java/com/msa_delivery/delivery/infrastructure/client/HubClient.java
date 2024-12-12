package com.msa_delivery.delivery.infrastructure.client;

import com.msa_delivery.delivery.application.dto.CommonResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "hub-service")
public interface HubClient {

    @GetMapping("/api/hubs/{hubId}")
    ResponseEntity<CommonResponse<HubDto>> getHubById(@PathVariable UUID hubId);
}
