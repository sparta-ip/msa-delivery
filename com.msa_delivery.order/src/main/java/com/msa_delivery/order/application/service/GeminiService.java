package com.msa_delivery.order.application.service;

import com.msa_delivery.order.application.dto.CompanyDataDto;
import com.msa_delivery.order.application.dto.GeminiRequestDto;
import com.msa_delivery.order.application.dto.GeminiResponseDto;
import com.msa_delivery.order.application.dto.HubDataDto;
import com.msa_delivery.order.application.dto.ProductDataDto;
import com.msa_delivery.order.application.feign.GeminiClient;
import com.msa_delivery.order.domain.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GeminiService {

    private final GeminiClient geminiClient;

    @Value("${googleai.api.key}")
    private String googleApiKey;

    // 최종 발송 시한 예측 요청
    public String predictFinalDeliveryTime(Order order, ProductDataDto productData,
        CompanyDataDto receiverData, HubDataDto supplierHubData, HubDataDto receiverHubData) {
        GeminiRequestDto geminiRequestDto = GeminiRequestDto.fromOrderData(
            productData.getName() + " " + order.getQuantity() + "박스",
            String.format("%s까지 도착해야 합니다.", order.getRequest()),
            supplierHubData.getName() + " (주소: " + supplierHubData.getAddress() + ")",
            receiverHubData.getName() + " (주소: " + receiverHubData.getAddress() + ")",
            receiverData.getAddress()
        );

        ResponseEntity<GeminiResponseDto> response = geminiClient.generateContent(geminiRequestDto, googleApiKey);
        GeminiResponseDto responseData = response.getBody();
        return responseData.getFinalDeliveryTime();
    }
}
