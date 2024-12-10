package com.msa_delivery.company.infrastructure.client;

import com.msa_delivery.company.application.dto.CommonResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "hub-service")
public interface HubClient {

    // search 로 받아오기 - 허브 서비스 구현 확인하면서 적절하게 변경
    @GetMapping("/api/hubs")
    ResponseEntity<CommonResponse<HubDto>> getHubs(@RequestParam("search") String name);
}