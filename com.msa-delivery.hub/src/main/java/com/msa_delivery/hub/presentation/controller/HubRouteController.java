package com.msa_delivery.hub.presentation.controller;


import com.msa_delivery.hub.application.dto.request.HubRouteSearch;
import com.msa_delivery.hub.application.service.HubRouteApplicationService;
import com.msa_delivery.hub.presentation.common.ApiResponse;
import com.msa_delivery.hub.presentation.response.HubRouteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hubs/hub-routes")
public class HubRouteController {

    private final HubRouteApplicationService hubRouteApplicationService;

    @GetMapping("/{hubRouteId}")
    public ApiResponse<HubRouteResponse> getHubRouteById(
            @PathVariable UUID hubRouteId,
            @RequestHeader(value = "X-User_Id", required = true) String userId,
            @RequestHeader(value = "X-Username", required = true) String username,
            @RequestHeader(value = "X-Role", required = true) String role
    ) {
        return ApiResponse.success(hubRouteApplicationService.getHubRouteById(hubRouteId));
    }

    @GetMapping
    public ApiResponse<Page<HubRouteResponse>> searchHubs(
            @ModelAttribute HubRouteSearch hubRouteSearch,
            @PathVariable UUID hubRouteId,
            @RequestHeader(value = "X-User_Id", required = true) String userId,
            @RequestHeader(value = "X-Username", required = true) String username,
            @RequestHeader(value = "X-Role", required = true) String role,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ApiResponse.success(hubRouteApplicationService.searchHubRouteList(hubRouteSearch, pageable));
    }
}
