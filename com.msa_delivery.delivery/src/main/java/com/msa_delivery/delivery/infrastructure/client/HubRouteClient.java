package com.msa_delivery.delivery.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "hub-service")
public interface HubRouteClient {

    // TODO: 파라미터 값 설정
    // 출발 허브와 도착 허브를 넣을 때, 허브 간 경로의 정보를 찾아주는 함수
    @GetMapping("/api/hub-routes")
    public HubRouteDto getHubRoute(@RequestParam UUID departureId, @RequestParam UUID arrivalId);
}
