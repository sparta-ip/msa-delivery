package com.msa_delivery.delivery.presentation.controller;

import com.msa_delivery.delivery.application.dto.CommonResponse;
import com.msa_delivery.delivery.application.dto.DeliveryCreateDto;
import com.msa_delivery.delivery.application.dto.DeliveryDto;
import com.msa_delivery.delivery.application.dto.DeliveryReadyDto;
import com.msa_delivery.delivery.application.service.DeliveryService;
import com.msa_delivery.delivery.presentation.request.DeliveryRequest;
import com.msa_delivery.delivery.presentation.request.DeliveryUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PostMapping
    public ResponseEntity<?> createDelivery(@RequestBody DeliveryRequest request,
                                           @RequestHeader("X-User_Id") String userId,
                                           @RequestHeader("X-Username") String username,
                                           @RequestHeader("X-Role") String role) {
        log.info("Received request to create delivery");
        DeliveryCreateDto delivery = deliveryService.createDelivery(request, userId, username, role);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                CommonResponse.success(HttpStatus.CREATED.value(), "배송 생성이 완료되었습니다.", delivery)
        );
    }

    @PutMapping("/{deliveryId}")
    public ResponseEntity<?> updateDelivery(@PathVariable UUID deliveryId,
                                            @RequestBody DeliveryUpdateRequest request,
                                            @RequestHeader("X-User_Id") String userId,
                                            @RequestHeader("X-Username") String username,
                                            @RequestHeader("X-Role") String role) throws AccessDeniedException {
        DeliveryDto delivery = deliveryService.updateDelivery(deliveryId, request, userId, username, role);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                CommonResponse.success(HttpStatus.CREATED.value(), "배송 정보 수정이 완료되었습니다.", delivery)
        );
    }

    @DeleteMapping("/{deliveryId}")
    public ResponseEntity<?> deleteDelivery(@PathVariable UUID deliveryId,
                                            @RequestHeader("X-User_Id") String userId,
                                            @RequestHeader("X-Username") String username,
                                            @RequestHeader("X-Role") String role) throws AccessDeniedException {
        deliveryService.deleteDelivery(deliveryId, userId, username, role);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                CommonResponse.success(HttpStatus.CREATED.value(), "배송 정보 삭제가 완료되었습니다.", null)
        );
    }

    @GetMapping("/{deliveryId}")
    public ResponseEntity<?> getDeliveryById(@PathVariable UUID deliveryId,
                                             @RequestHeader("X-User_Id") String userId,
                                             @RequestHeader("X-Username") String username,
                                             @RequestHeader("X-Role") String role) throws AccessDeniedException {
        DeliveryReadyDto delivery = deliveryService.getDeliveryById(deliveryId, userId, username, role);
        return ResponseEntity.status(HttpStatus.OK).body(
                CommonResponse.success(HttpStatus.OK.value(), "배송 상세 조회가 완료되었습니다.", delivery)
        );
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
            @RequestHeader("X-User_Id") String userId,
            @RequestHeader("X-Username") String username,
            @RequestHeader("X-Role") String role,
            Pageable pageable) throws AccessDeniedException {
        Page<DeliveryDto> deliveries =
                deliveryService.getDeliveries(search, deliveryStatus, departureId, arrivalId, deliveryManagerId,
                        receiverId, createdFrom, createdTo, userId, username, role, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(
                CommonResponse.success(HttpStatus.OK.value(), "검색 조회가 완료되었습니다.", deliveries)
        );
    }

}
