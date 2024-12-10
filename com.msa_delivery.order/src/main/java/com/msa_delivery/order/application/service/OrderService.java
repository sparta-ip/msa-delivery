package com.msa_delivery.order.application.service;

import com.msa_delivery.order.application.dto.CompanyDataDto;
import com.msa_delivery.order.application.dto.HubDataDto;
import com.msa_delivery.order.application.dto.OrderDataDto;
import com.msa_delivery.order.application.dto.OrderRequestDto;
import com.msa_delivery.order.application.dto.ProductDataDto;
import com.msa_delivery.order.application.dto.ResponseDto;
import com.msa_delivery.order.application.exception.OrderCreationException;
import com.msa_delivery.order.application.exception.ProductNotFoundException;
import com.msa_delivery.order.domain.model.Order;
import com.msa_delivery.order.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final ProductService productService;
    private final CompanyService companyService;
    private final DeliveryService deliveryService;
    private final GeminiService geminiService;
    private final HubService hubService;

    private final OrderRepository orderRepository;

    @Value("${googleai.api.key}")
    private String googleApiKey;

    public ResponseDto<OrderDataDto> createOrder(OrderRequestDto.Create orderRequestDto, String username) {

        try {
            // 상품 확인 및 수량 감소
            ProductDataDto productData = productService.getProduct(orderRequestDto.getProduct_id());
            if (productData == null) {
                throw new ProductNotFoundException("Product not found for id: " + orderRequestDto.getProduct_id());
            }
            productService.reduceProductQuantity(productData.getProduct_id(), orderRequestDto.getQuantity());

            // 업체 확인
            CompanyDataDto receiverData = companyService.getCompany(orderRequestDto.getReceiver_id());
            CompanyDataDto supplierData = companyService.getCompany(orderRequestDto.getSupplier_id());

            if (receiverData == null || supplierData == null) {
                throw new OrderCreationException("Receiver or Supplier not found.");
            }

            // 주문 생성 및 저장
            Order order = Order.createOrder(orderRequestDto);
            Order savedOrder = orderRepository.save(order);

            // 배송 요청 생성
            deliveryService.createDelivery(savedOrder, receiverData, supplierData);

            // 업체 허브 정보확인
            HubDataDto receiverHubData = hubService.getHub(receiverData.getHub_id());
            HubDataDto supplierHubData = hubService.getHub(supplierData.getHub_id());

            // AI 예측 요청
            String finalDeliveryTime = geminiService.predictFinalDeliveryTime(savedOrder, productData, receiverData, supplierHubData, receiverHubData);
            log.info("최종 발송 시한: {}", finalDeliveryTime);

            // 응답 데이터 생성
            OrderDataDto orderDataDto = new OrderDataDto(savedOrder);
            return new ResponseDto<>(HttpStatus.OK.value(), "주문이 생성되었습니다.", orderDataDto);

        } catch (ProductNotFoundException | OrderCreationException e) {
            // 예외가 발생한 경우, 전역 예외 처리기가 처리하도록 던짐
            throw e;
        } catch (Exception e) {
            // 예기치 못한 예외 발생 시, OrderCreationException 처리
            throw new OrderCreationException("주문 생성 중 예기치 못한 오류가 발생했습니다.");
        }

    }

    private ResponseDto<OrderDataDto> toResponseDto(HttpStatusCode statusCode, String message, Order order) {

        OrderDataDto orderDataDto = new OrderDataDto(
            order.getOrder_id(),
            order.getDelivery_id(),
            order.getStatus(),
            order.getCreated_at(),
            order.getCreated_by(),
            order.getUpdated_at(),
            order.getUpdated_by()
        );

        return new ResponseDto<>(
            statusCode.value(),
            message,
            orderDataDto
        );
    }
}
