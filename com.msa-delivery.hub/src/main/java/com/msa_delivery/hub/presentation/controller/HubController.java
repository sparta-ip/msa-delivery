package com.msa_delivery.hub.presentation.controller;

import com.msa_delivery.hub.application.dto.request.CreateHubReqDto;
import com.msa_delivery.hub.application.dto.request.HubSearch;
import com.msa_delivery.hub.application.service.HubApplicationService;
import com.msa_delivery.hub.presentation.common.ApiResponse;
import com.msa_delivery.hub.presentation.response.HubRes;
import com.msa_delivery.hub.presentation.response.HubWithRoutesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hubs")
public class HubController {

    private final HubApplicationService hubApplicationService;


    @PostMapping
    public ApiResponse<HubWithRoutesResponse> createHub(@RequestBody CreateHubReqDto createHubReqDto, @RequestHeader(value = "X-userId", required = true)Long userId) {
        return ApiResponse.success(hubApplicationService.createHubWithRoutes(createHubReqDto, userId));
    }

    @PatchMapping("/{hubId}")
    public ApiResponse<HubWithRoutesResponse> updateHub(@PathVariable UUID hubId, @RequestBody CreateHubReqDto createHubReqDto , @RequestHeader(value = "X-userId", required = true) Long userId)  {
        return ApiResponse.success(hubApplicationService.updateHub(hubId ,createHubReqDto,userId));
    }

    @DeleteMapping("/{hubId}")
    public ApiResponse<String> deleteHub(@PathVariable UUID hubId , @RequestHeader(value = "X-userId", required = true) Long userId) {
        hubApplicationService.deleteHub(hubId, userId);
        return ApiResponse.success(userId + "허브 필드가 비활성화 되었습니다.");
    }
    @GetMapping("/search")
    public ApiResponse<Page<HubRes>> searchHubs(
            @ModelAttribute HubSearch hubSearch,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ApiResponse.success(hubApplicationService.searchHubs(hubSearch, pageable));
    }
}

