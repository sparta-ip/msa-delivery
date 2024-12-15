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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hubs/hub-route")
public class HubRouteController {

    private final HubRouteApplicationService hubRouteApplicationService;

    @PostMapping()
    public ApiResponse<List<HubRouteResponse>> createHubRoute(@RequestHeader(value = "X-UserId", required = true) Long userId) {
        return ApiResponse.success(hubRouteApplicationService.createHubRouteList(userId));
    }

    @GetMapping("/search")
    public ApiResponse<Page<HubRouteResponse>> searchHubs(
            @ModelAttribute HubRouteSearch hubRouteSearch,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ApiResponse.success(hubRouteApplicationService.searchHubRouteList(hubRouteSearch, pageable));
    }
}
