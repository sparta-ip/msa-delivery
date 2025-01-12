package com.msa_delivery.delivery.presentation.controller;

import com.msa_delivery.delivery.application.dto.CommonResponse;
import com.msa_delivery.delivery.application.dto.DeliveryDto;
import com.msa_delivery.delivery.application.dto.DeliveryRouteDto;
import com.msa_delivery.delivery.application.service.DeliveryRouteService;
import com.msa_delivery.delivery.presentation.request.DeliveryRouteRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

@RestController
@RequestMapping("/api/deliveries/delivery-routes")
@RequiredArgsConstructor
public class DeliveryRouteController {

    private final DeliveryRouteService deliveryRouteService;

    @PutMapping("/{deliveryRouteId}")
    public ResponseEntity<?> updateDelivery(@PathVariable UUID deliveryRouteId,
                                            @RequestBody DeliveryRouteRequest request,
                                            @RequestHeader("X-User_Id") String userId,
                                            @RequestHeader("X-Username") String username,
                                            @RequestHeader("X-Role") String role) throws AccessDeniedException {
        DeliveryRouteDto route = deliveryRouteService.updateRoute(deliveryRouteId, request, userId, username, role);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                CommonResponse.success(HttpStatus.CREATED.value(), "배송 경로 정보 수정이 완료되었습니다.", route)
        );
    }

    @DeleteMapping("/{deliveryRouteId}")
    public ResponseEntity<?> deleteRoute(@PathVariable UUID deliveryRouteId,
                                            @RequestHeader("X-User_Id") String userId,
                                            @RequestHeader("X-Username") String username,
                                            @RequestHeader("X-Role") String role) throws AccessDeniedException {
        deliveryRouteService.deleteRoute(deliveryRouteId, userId, username, role);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                CommonResponse.success(HttpStatus.CREATED.value(), "배송 경로 정보 삭제가 완료되었습니다.", null)
        );

    }

    @GetMapping("/{deliveryRouteId}")
    public ResponseEntity<?> getRouteById(@PathVariable UUID deliveryRouteId,
                                          @RequestHeader("X-User_Id") String userId,
                                          @RequestHeader("X-Username") String username,
                                          @RequestHeader("X-Role") String role) throws AccessDeniedException {
        DeliveryRouteDto route = deliveryRouteService.getRouteById(deliveryRouteId, userId, username, role);
        return ResponseEntity.status(HttpStatus.OK).body(
                CommonResponse.success(HttpStatus.OK.value(), "배송 상세 조회가 완료되었습니다.", route)
        );
    }

    @GetMapping
    public ResponseEntity<?> getRoutes(
            @RequestParam(value = "delivery_status", required = false) String deliveryStatus,
            @RequestParam(value = "departure_id", required = false) UUID departureId,
            @RequestParam(value = "arrival_id", required = false) UUID arrivalId,
            @RequestParam(value = "delivery_manager_id", required = false) Long deliveryManagerId,
            @RequestParam(value = "created_from", required = false) String createdFrom,
            @RequestParam(value = "created_to", required = false) String createdTo,
            @RequestHeader("X-User_Id") String userId,
            @RequestHeader("X-Username") String username,
            @RequestHeader("X-Role") String role,
            Pageable pageable) throws AccessDeniedException {
        Page<DeliveryRouteDto> deliveryRoutes =
                deliveryRouteService.getRoutes(deliveryStatus, departureId, arrivalId, deliveryManagerId,
                        createdFrom, createdTo, userId, username, role, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(
                CommonResponse.success(HttpStatus.OK.value(), "검색 조회가 완료되었습니다.", deliveryRoutes)
        );
    }

}
