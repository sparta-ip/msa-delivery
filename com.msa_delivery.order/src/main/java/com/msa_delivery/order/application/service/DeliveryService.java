package com.msa_delivery.order.application.service;

import com.msa_delivery.order.application.dto.CompanyDataDto;
import com.msa_delivery.order.application.dto.DeliveryRequestDto;
import com.msa_delivery.order.application.dto.DeliveryResponseDto;
import com.msa_delivery.order.application.dto.ResponseDto;
import com.msa_delivery.order.application.feign.DeliveryClient;
import com.msa_delivery.order.domain.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryClient deliveryClient;

    // 배송 요청 생성
    public DeliveryResponseDto createDelivery(Order order, CompanyDataDto receiverData, CompanyDataDto supplierData) {
        DeliveryRequestDto requestDto = new DeliveryRequestDto(
            order.getOrder_id(),
            receiverData.getCompany_manager_id(),
            receiverData.getSlack_id(),
            receiverData.getHub_id(),
            supplierData.getHub_id(),
            receiverData.getAddress()
        );
        ResponseDto<DeliveryResponseDto> response = deliveryClient.createDelivery(requestDto);
        return response.getData();
    }
}
