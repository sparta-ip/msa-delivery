package com.msa_delivery.order.application.service;

import com.msa_delivery.order.application.dto.CompanyDataDto;
import com.msa_delivery.order.application.dto.DeliveryManagerResponseDto;
import com.msa_delivery.order.application.dto.DeliveryRequestDto;
import com.msa_delivery.order.application.dto.DeliveryResponseDto;
import com.msa_delivery.order.application.dto.ResponseDto;
import com.msa_delivery.order.application.feign.DeliveryClient;
import com.msa_delivery.order.domain.model.Order;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
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
            supplierData.getHub_id(),
            receiverData.getHub_id(),
            receiverData.getAddress()
        );
        log.info("배송 요청 dto 생성");

        ResponseEntity<ResponseDto<DeliveryResponseDto>> response = deliveryClient.createDelivery(requestDto);

        log.info("배송 요청 dto 확인 : " + response.toString());
        return response.getBody().getData();
    }

    // 특정 주문에 해당하는 배송 담당자 ID 목록 조회
    public List<Long> getDeliveryManagerIdsByOrderId(UUID order_id) {
        ResponseDto<List<DeliveryManagerResponseDto>> response = deliveryClient.getDeliveryManagerIdsByOrderId(order_id);

        if (response.getData() == null || response.getData().isEmpty()) {
            throw new RuntimeException("배송 담당자 정보 조회 실패");
        }

        // Stream을 사용하여 delivery_manager_id만 추출
        return response.getData().stream()
            .map(DeliveryManagerResponseDto::getDelivery_manager_id) // DeliveryManagerResponseDto에서 delivery_manager_id만 추출
            .collect(Collectors.toList());
    }
}
