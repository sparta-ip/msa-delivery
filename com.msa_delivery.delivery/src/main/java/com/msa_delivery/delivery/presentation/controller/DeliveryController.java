package com.msa_delivery.delivery.presentation.controller;

import com.msa_delivery.delivery.application.dto.CommonResponse;
import com.msa_delivery.delivery.application.dto.DeliveryCreateDto;
import com.msa_delivery.delivery.application.dto.DeliveryDto;
import com.msa_delivery.delivery.application.service.DeliveryService;
import com.msa_delivery.delivery.presentation.request.DeliveryRequest;
import com.msa_delivery.delivery.presentation.request.DeliveryUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PostMapping
    public ResponseEntity<?> createDelivery(@Valid @RequestBody DeliveryRequest request,
                                           @RequestHeader("X-User_Id") String userId,
                                           @RequestHeader("X-Username") String username,
                                           @RequestHeader("X-Role") String role) {
        try {
            DeliveryCreateDto delivery = deliveryService.createDelivery(request, userId, username, role);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    CommonResponse.success(HttpStatus.CREATED, "배송 생성이 완료되었습니다.", delivery)
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    CommonResponse.error(HttpStatus.FORBIDDEN, e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    CommonResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "배송 생성 중 오류가 발생했습니다.")
            );
        }
    }

    @PutMapping("/{deliveryId}")
    public ResponseEntity<?> updateDelivery(@PathVariable UUID deliveryId,
                                            @RequestBody DeliveryUpdateRequest request,
                                            @RequestHeader("X-User_Id") String userId,
                                            @RequestHeader("X-Username") String username,
                                            @RequestHeader("X-Role") String role) {
        try {
            DeliveryDto delivery = deliveryService.updateDelivery(deliveryId, request, userId, username, role);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    CommonResponse.success(HttpStatus.CREATED, "배송 정보 수정이 완료되었습니다.", delivery)
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    CommonResponse.error(HttpStatus.FORBIDDEN, e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    CommonResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "배송 정보 수정 중 오류가 발생했습니다.")
            );
        }
    }

    @DeleteMapping("/{deliveryId}")
    public ResponseEntity<?> deleteDelivery(@PathVariable UUID deliveryId,
                                            @RequestHeader("X-User_Id") String userId,
                                            @RequestHeader("X-Username") String username,
                                            @RequestHeader("X-Role") String role) {
        try {
            deliveryService.deleteDelivery(deliveryId, userId, username, role);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    CommonResponse.success(HttpStatus.CREATED, "배송 정보 삭제가 완료되었습니다.")
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    CommonResponse.error(HttpStatus.FORBIDDEN, e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    CommonResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "배송 정보 삭제 중 오류가 발생했습니다.")
            );
        }
    }

    @GetMapping("/{deliveryId}")
    public ResponseEntity<?> getDeliveryById(@PathVariable UUID deliveryId,
                                            @RequestHeader("X-User_Id") Long userId,
                                            @RequestHeader("X-Role") String role) {
        try {
            DeliveryDto delivery = deliveryService.getDeliveryById(deliveryId, userId, role);
            return ResponseEntity.status(HttpStatus.OK).body(
                    CommonResponse.success(HttpStatus.OK, "배송 상세 조회가 완료되었습니다.", delivery)
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
    public ResponseEntity<?> getDeliveries(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "delivery_status", required = false) String deliveryStatus,
            @RequestParam(value = "departure_id", required = false) UUID departureId,
            @RequestParam(value = "arrival_id", required = false) UUID arrivalId,
            @RequestParam(value = "delivery_manager_id", required = false) Long deliveryManagerId,
            @RequestParam(value = "receiver_id", required = false) Long receiverId,
            @RequestParam(value = "created_from", required = false) String createdFrom,
            @RequestParam(value = "created_to", required = false) String createdTo,
            @RequestHeader("X-User_Id") Long userId,
            @RequestHeader("X-Role") String role,
            Pageable pageable) {
        try {
            Page<DeliveryDto> deliveries =
                    deliveryService.getDeliveries(search, deliveryStatus, departureId, arrivalId, deliveryManagerId,
                            receiverId, createdFrom, createdTo, userId, role, pageable);

            return ResponseEntity.status(HttpStatus.OK).body(
                    CommonResponse.success(HttpStatus.OK, "검색 조회가 완료되었습니다.", deliveries)
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
