package com.msa_delivery.order.application.feign;

import com.msa_delivery.order.application.dto.DeliveryManagerResponseDto;
import com.msa_delivery.order.application.dto.DeliveryRequestDto;
import com.msa_delivery.order.application.dto.DeliveryResponseDto;
import com.msa_delivery.order.application.dto.ResponseDto;
import com.msa_delivery.order.infrastructure.configuration.FeignClientConfig;
import java.util.List;
import java.util.UUID;
import org.hibernate.query.Page;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="delivery-service", configuration = FeignClientConfig.class)
public interface DeliveryClient {

    @PostMapping("/api/deliveries")
    ResponseDto<DeliveryResponseDto> createDelivery(@RequestBody DeliveryRequestDto deliveryRequestDto);

    // 특정 주문의 배송 담당자 ID 목록 조회
    @GetMapping("/api/deliveries/delivery-managers")
    ResponseDto<List<DeliveryManagerResponseDto>> getDeliveryManagerIdsByOrderId(@RequestParam UUID order_id);
}
