package com.msa_delivery.company.infrastructure.client;

import com.msa_delivery.company.application.dto.CommonResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "hub-service")
public interface HubClient {

    // search 로 받아오기 - 허브 서비스 구현 확인하면서 적절하게 변경
    @GetMapping("/api/hubs")
    ResponseEntity<CommonResponse<Page<HubDto>>> getHubs(@RequestParam("name") String name,
                                                         @RequestHeader("X-User_Id") String userId,
                                                         @RequestHeader("X-Username") String username,
                                                         @RequestHeader("X-Role") String role);

    @GetMapping("/api/hubs/{hubId}")
    ResponseEntity<CommonResponse<HubDto>> getHubById(@PathVariable UUID hubId,
                                                      @RequestHeader("X-User_Id") String userId,
                                                      @RequestHeader("X-Username") String username,
                                                      @RequestHeader("X-Role") String role);
}