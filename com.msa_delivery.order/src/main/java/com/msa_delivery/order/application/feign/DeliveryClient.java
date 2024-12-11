package com.msa_delivery.order.application.feign;

import com.msa_delivery.order.application.dto.DeliveryRequestDto;
import com.msa_delivery.order.application.dto.DeliveryResponseDto;
import com.msa_delivery.order.application.dto.ResponseDto;
import java.util.List;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="delivery-service")
public interface DeliveryClient {

    @PostMapping("/api/deliveries")
    ResponseDto<DeliveryResponseDto> createDelivery(DeliveryRequestDto deliveryRequestDto);

    // 특정 주문의 배송 담당자 ID 목록 조회
    @GetMapping("/api/delivery-managers/order_id")
    ResponseDto<List<Long>> getDeliveryManagerIdsByOrderId(@RequestParam UUID order_id);
}
