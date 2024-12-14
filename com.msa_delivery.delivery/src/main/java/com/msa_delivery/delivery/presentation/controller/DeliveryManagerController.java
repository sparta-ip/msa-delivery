package com.msa_delivery.delivery.presentation.controller;

import com.msa_delivery.delivery.application.dto.CommonResponse;
import com.msa_delivery.delivery.application.dto.DeliveryManagerDto;
import com.msa_delivery.delivery.application.service.DeliveryManagerService;
import com.msa_delivery.delivery.presentation.request.DeliveryManagerRequest;
import com.msa_delivery.delivery.presentation.request.DeliveryManagerUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

@RestController
@RequestMapping("/api/deliveries/delivery-managers")
@RequiredArgsConstructor
public class DeliveryManagerController {

    private final DeliveryManagerService deliveryManagerService;

    @PostMapping
    public ResponseEntity<?> createManager(@Valid @RequestBody DeliveryManagerRequest request,
                                            @RequestHeader("X-User_Id") String userId,
                                            @RequestHeader("X-Username") String username,
                                            @RequestHeader("X-Role") String role) throws AccessDeniedException {
        DeliveryManagerDto manager = deliveryManagerService.createManager(request, userId, username, role);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                CommonResponse.success(HttpStatus.CREATED, "배송 담당자 생성이 완료되었습니다.", manager)
        );
    }

    @PutMapping("/{deliveryManagerId}")
    public ResponseEntity<?> updateManager(@PathVariable Long deliveryManagerId,
                                           @RequestBody DeliveryManagerUpdateRequest request,
                                           @RequestHeader("X-User_Id") String userId,
                                           @RequestHeader("X-Username") String username,
                                           @RequestHeader("X-Role") String role) throws AccessDeniedException {
        DeliveryManagerDto manager = deliveryManagerService.updateManager(deliveryManagerId, request, userId, username, role);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                CommonResponse.success(HttpStatus.CREATED, "배송 담당자 정보 수정이 완료되었습니다.", manager)
        );
    }

    @DeleteMapping("/{deliveryManagerId}")
    public ResponseEntity<?> deleteManager(@PathVariable Long deliveryManagerId,
                                         @RequestHeader("X-User_Id") String userId,
                                         @RequestHeader("X-Username") String username,
                                         @RequestHeader("X-Role") String role) throws AccessDeniedException {
        deliveryManagerService.deleteManager(deliveryManagerId, userId, username, role);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                CommonResponse.success(HttpStatus.CREATED, "배송 담당자 정보 삭제가 완료되었습니다.")
        );
    }

    @GetMapping("/{deliveryManagerId}")
    public ResponseEntity<?> getManagerById(@PathVariable Long deliveryManagerId,
                                            @RequestHeader("X-User_Id") String userId,
                                            @RequestHeader("X-Username") String username,
                                            @RequestHeader("X-Role") String role) throws AccessDeniedException {
        DeliveryManagerDto manager = deliveryManagerService.getManagerById(deliveryManagerId, userId, username, role);
        return ResponseEntity.status(HttpStatus.OK).body(
                CommonResponse.success(HttpStatus.OK, "배송 담당자 상세 조회가 완료되었습니다.", manager)
        );
    }

    @GetMapping
    public ResponseEntity<?> getManagers(
            @RequestParam(value = "search", required = false) String search, // 배송 담당자 타입
            @RequestParam(value = "type", required = false) String type, // 배송 담당자 타입
            @RequestParam(value = "delivery_manager_id", required = false) Long deliveryManagerId, // 허브 ID
            @RequestParam(value = "hub_id", required = false) UUID hubId, // 허브 ID
            @RequestParam(value = "order_id", required = false) UUID orderId, // 허브 ID
            @RequestParam(value = "sequence_min", required = false) Integer sequenceMin, // 최소 순번
            @RequestParam(value = "sequence_max", required = false) Integer sequenceMax, // 최대 순번
            @RequestParam(value = "created_from", required = false) String createdFrom, // 생성 시작일
            @RequestParam(value = "created_to", required = false) String createdTo, // 생성 종료일
            @RequestHeader("X-User_Id") String userId,
            @RequestHeader("X-Username") String username,
            @RequestHeader("X-Role") String role,
            Pageable pageable) throws AccessDeniedException {
        Page<DeliveryManagerDto> managers =
                deliveryManagerService.getManagers(search, type, deliveryManagerId, hubId, orderId, sequenceMin, sequenceMax,
                        createdFrom, createdTo, userId, role, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(
                CommonResponse.success(HttpStatus.OK, "검색 조회가 완료되었습니다.", managers)
        );
    }
}
