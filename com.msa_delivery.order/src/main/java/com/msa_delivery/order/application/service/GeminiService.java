package com.msa_delivery.order.application.service;

import com.msa_delivery.order.application.dto.CompanyDataDto;
import com.msa_delivery.order.application.dto.GeminiRequestDto;
import com.msa_delivery.order.application.dto.GeminiResponseDto;
import com.msa_delivery.order.application.dto.HubDataDto;
import com.msa_delivery.order.application.dto.ProductDataDto;
import com.msa_delivery.order.application.exception.GeminiServiceException;
import com.msa_delivery.order.application.feign.GeminiClient;
import com.msa_delivery.order.domain.model.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiService {

    private final GeminiClient geminiClient;

    @Value("${googleai.api.key}")
    private String googleApiKey;

    // 최종 발송 시한 예측 요청
    public String predictFinalDeliveryTime(Order order, ProductDataDto productData,
        CompanyDataDto receiverData, HubDataDto supplierHubData, HubDataDto receiverHubData) {
        String finalDeliveryTime = null; // 반환할 최종 발송 시한

        try {
            // 요청 데이터 생성
            GeminiRequestDto geminiRequestDto = GeminiRequestDto.fromOrderData(
                productData.getName() + " " + order.getQuantity() + "박스",
                String.format("%s까지 도착해야 합니다.", order.getRequest()),
                supplierHubData.getHub_name() + " (주소: " + supplierHubData.getAddress() + ")",
                receiverHubData.getHub_name() + " (주소: " + receiverHubData.getAddress() + ")",
                receiverData.getAddress()
            );

            log.info("GeminiRequestDto: {}", geminiRequestDto.toString());

            // OpenFeign 클라이언트로 외부 API 호출
            ResponseEntity<GeminiResponseDto> response = geminiClient.generateContent(geminiRequestDto, googleApiKey);

            log.info("Response status: {}", response.getStatusCode());

            if (response.getBody() != null) {
                GeminiResponseDto responseData = response.getBody();
                finalDeliveryTime = responseData.getFinalDeliveryTime();
            } else {
                log.warn("Response body is null");
            }
        }  catch (feign.FeignException e) {
            log.error("Feign Client 호출 중 FeignException 발생: {}", e.getMessage(), e);
            throw new GeminiServiceException("Gemini API 호출 실패", e);
        } catch (Exception e) {
            log.error("예기치 못한 예외 발생: {}", e.getMessage(), e);
            throw new GeminiServiceException("알 수 없는 오류 발생", e);
        }

        return finalDeliveryTime;
    }
}
