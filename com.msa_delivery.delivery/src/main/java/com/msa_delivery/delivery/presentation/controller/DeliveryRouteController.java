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
                                            @RequestHeader("X-Role") String role) {
        try {
            DeliveryRouteDto route = deliveryRouteService.updateRoute(deliveryRouteId, request, userId, username, role);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    CommonResponse.success(HttpStatus.CREATED, "배송 경로 정보 수정이 완료되었습니다.", route)
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    CommonResponse.error(HttpStatus.FORBIDDEN, e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    CommonResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "배송 경로 정보 수정 중 오류가 발생했습니다.")
            );
        }
    }

    @DeleteMapping("/{deliveryRouteId}")
    public ResponseEntity<?> deleteRoute(@PathVariable UUID deliveryRouteId,
                                            @RequestHeader("X-User_Id") String userId,
                                            @RequestHeader("X-Username") String username,
                                            @RequestHeader("X-Role") String role) {
        try {
            deliveryRouteService.deleteRoute(deliveryRouteId, userId, username, role);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    CommonResponse.success(HttpStatus.CREATED, "배송 경로 정보 삭제가 완료되었습니다.")
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    CommonResponse.error(HttpStatus.FORBIDDEN, e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    CommonResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "배송 경로 삭제 중 오류가 발생했습니다.")
            );
        }
    }

    @GetMapping("/{deliveryRouteId}")
    public ResponseEntity<?> getRouteById(@PathVariable UUID deliveryRouteId,
                                          @RequestHeader("X-User_Id") String userId,
                                          @RequestHeader("X-Username") String username,
                                          @RequestHeader("X-Role") String role) {
        try {
            DeliveryRouteDto route = deliveryRouteService.getRouteById(deliveryRouteId, userId, username, role);
            return ResponseEntity.status(HttpStatus.OK).body(
                    CommonResponse.success(HttpStatus.OK, "배송 상세 조회가 완료되었습니다.", route)
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    CommonResponse.error(HttpStatus.NOT_FOUND, e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    CommonResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "배송 조회 중 오류가 발생했습니다.")
            );
        }
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
            Pageable pageable) {
        try {
            Page<DeliveryRouteDto> deliveryRoutes =
                    deliveryRouteService.getRoutes(deliveryStatus, departureId, arrivalId, deliveryManagerId,
                            createdFrom, createdTo, userId, username, role, pageable);

            return ResponseEntity.status(HttpStatus.OK).body(
                    CommonResponse.success(HttpStatus.OK, "검색 조회가 완료되었습니다.", deliveryRoutes)
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    CommonResponse.error(HttpStatus.FORBIDDEN, e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    CommonResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "검색 중 오류가 발생했습니다.")
            );
        }
    }

}
