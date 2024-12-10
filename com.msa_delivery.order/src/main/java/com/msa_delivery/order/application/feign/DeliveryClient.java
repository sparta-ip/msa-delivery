package com.msa_delivery.order.application.feign;

import com.msa_delivery.order.application.dto.DeliveryRequestDto;
import com.msa_delivery.order.application.dto.DeliveryResponseDto;
import com.msa_delivery.order.application.dto.ResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name="delivery-service")
public interface DeliveryClient {

    @PostMapping("/api/deliveries")
    ResponseDto<DeliveryResponseDto> createDelivery(DeliveryRequestDto deliveryRequestDto);

}
