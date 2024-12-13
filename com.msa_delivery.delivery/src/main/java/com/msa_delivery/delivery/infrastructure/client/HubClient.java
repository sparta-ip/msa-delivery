package com.msa_delivery.delivery.infrastructure.client;

import com.msa_delivery.delivery.application.dto.CommonResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "hub-service")
public interface HubClient {

    @GetMapping("/api/hubs/{hubId}")
    ResponseEntity<CommonResponse<HubDto>> getHubById(@PathVariable UUID hubId,
                                                      @RequestHeader("X-User_Id") String userId,
                                                      @RequestHeader("X-Username") String username,
                                                      @RequestHeader("X-Role") String role);

    // 출발 허브와 도착 허브를 넣을 때, 허브 간 경로의 정보를 찾아주는 함수 (search)
    @GetMapping("/api/hub-routes")
    ResponseEntity<CommonResponse<HubRouteDto>> getHubRoute(@RequestParam UUID departureId, @RequestParam UUID arrivalId,
                                                            @RequestHeader("X-User_Id") String userId,
                                                            @RequestHeader("X-Username") String username,
                                                            @RequestHeader("X-Role") String role);
}
