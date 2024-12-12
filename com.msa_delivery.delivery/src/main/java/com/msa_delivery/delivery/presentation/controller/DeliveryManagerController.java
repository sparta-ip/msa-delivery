package com.msa_delivery.delivery.presentation.controller;

import com.msa_delivery.delivery.application.dto.CommonResponse;
import com.msa_delivery.delivery.application.dto.DeliveryManagerDto;
import com.msa_delivery.delivery.application.service.DeliveryManagerService;
import com.msa_delivery.delivery.presentation.request.DeliveryManagerRequest;
import com.msa_delivery.delivery.presentation.request.DeliveryManagerUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/delivery-managers")
@RequiredArgsConstructor
public class DeliveryManagerController {

    private final DeliveryManagerService deliveryManagerService;

    @PostMapping
    public ResponseEntity<?> createManager(@Valid @RequestBody DeliveryManagerRequest request,
                                            @RequestHeader("X-User_Id") String userId,
                                            @RequestHeader("X-Username") String username,
                                            @RequestHeader("X-Role") String role) {
        try {
            DeliveryManagerDto manager = deliveryManagerService.createManager(request, userId, username, role);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    CommonResponse.success(HttpStatus.CREATED, "배송 담당자 생성이 완료되었습니다.", manager)
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    CommonResponse.error(HttpStatus.FORBIDDEN, e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    CommonResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "배송 담당자 생성 중 오류가 발생했습니다.")
            );
        }
    }

    @PutMapping("/{deliveryManagerId}")
    public ResponseEntity<?> updateManager(@PathVariable Long deliveryManagerId,
                                           @RequestBody DeliveryManagerUpdateRequest request,
                                           @RequestHeader("X-User_Id") String userId,
                                           @RequestHeader("X-Username") String username,
                                           @RequestHeader("X-Role") String role) {
        try {
            DeliveryManagerDto manager = deliveryManagerService.updateManager(deliveryManagerId, request, userId, username, role);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    CommonResponse.success(HttpStatus.CREATED, "배송 담당자 정보 수정이 완료되었습니다.", manager)
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    CommonResponse.error(HttpStatus.FORBIDDEN, e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    CommonResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "배송 담당자 정보 수정 중 오류가 발생했습니다.")
            );
        }
    }
}
