package com.msa_delivery.order.application.service;

import com.msa_delivery.order.application.dto.CompanyDataDto;
import com.msa_delivery.order.application.dto.HubDataDto;
import com.msa_delivery.order.application.dto.OrderDataDto;
import com.msa_delivery.order.application.dto.OrderRequestDto;
import com.msa_delivery.order.application.dto.ProductDataDto;
import com.msa_delivery.order.application.dto.ResponseDto;
import com.msa_delivery.order.application.exception.OrderCreationException;
import com.msa_delivery.order.application.exception.OrderNotFoundException;
import com.msa_delivery.order.application.exception.ProductNotFoundException;
import com.msa_delivery.order.domain.model.Order;
import com.msa_delivery.order.domain.model.OrderStatus;
import com.msa_delivery.order.domain.repository.OrderRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // 주문 생성
    @Transactional
    public ResponseDto<OrderDataDto> createOrder(OrderRequestDto.Create orderRequestDto,
        String username) {

        try {
            // 상품 확인
            ProductDataDto productData = productService.getProduct(orderRequestDto.getProduct_id());
            if (productData == null) {
                throw new ProductNotFoundException(
                    "Product not found for id: " + orderRequestDto.getProduct_id());
            }
            // 재고 확인
            if (productData.getQuantity() < orderRequestDto.getQuantity()) {
                throw new OrderCreationException("Quantity exceeds maximum quantity");
            }

            // 업체 확인
            CompanyDataDto receiverData = companyService.getCompany(
                orderRequestDto.getReceiver_id());
            CompanyDataDto supplierData = companyService.getCompany(
                orderRequestDto.getSupplier_id());
            if (receiverData == null || supplierData == null) {
                throw new OrderCreationException("Receiver or Supplier not found.");
            }

            // 업체 허브 정보확인
            HubDataDto receiverHubData = hubService.getHub(receiverData.getHub_id());
            HubDataDto supplierHubData = hubService.getHub(supplierData.getHub_id());
            if (receiverHubData == null || supplierHubData == null) {
                throw new OrderCreationException("Receiver or Hub not found.");
            }

            // 재고 차감
            try {
                productService.reduceProductQuantity(orderRequestDto.getProduct_id(),
                    orderRequestDto.getQuantity());
            } catch (Exception e) {
                throw new OrderCreationException("재고 차감 중 오류가 발생했습니다.");
            }

            try {
                // 주문 생성 및 저장
                Order order = Order.createOrder(orderRequestDto);
                Order savedOrder = orderRepository.save(order);

                // 배송 요청 및 AI 예측
                deliveryService.createDelivery(savedOrder, receiverData, supplierData);
                String finalDeliveryTime = geminiService.predictFinalDeliveryTime(savedOrder,
                    productData, receiverData, supplierHubData, receiverHubData);
                log.info("최종 발송 시한: {}", finalDeliveryTime);

                // 응답 데이터 생성
                OrderDataDto orderDataDto = new OrderDataDto(savedOrder);
                return new ResponseDto<>(HttpStatus.OK.value(), "주문이 생성되었습니다.", orderDataDto);

            } catch (Exception e) {
                // 보상 트랜잭션 (재고 복구)
                try {
                    productService.reduceProductQuantity(orderRequestDto.getProduct_id(),
                        -orderRequestDto.getQuantity());
                    log.info("보상 트랜잭션 수행: 상품 ID {}의 재고를 복구했습니다.", orderRequestDto.getProduct_id());
                } catch (Exception restoreException) {
                    log.error("보상 트랜잭션 중 오류 발생: {}", restoreException.getMessage());
                }
                throw new OrderCreationException("주문 생성 중 오류가 발생했습니다.");
            }

        } catch (ProductNotFoundException | OrderCreationException e) {
            // 예외가 발생한 경우, 전역 예외 처리기가 처리하도록 던짐
            throw e;
        } catch (Exception e) {
            // 예기치 못한 예외 발생 시, OrderCreationException 처리
            throw new OrderCreationException("주문 생성 중 예기치 못한 오류가 발생했습니다.");
        }

    }

    // 특정 주문과 관련 있는 업체의 허브 ID 가져오기
    @Transactional(readOnly = true)
    public List<UUID> getHubIdByOrderId(UUID order_id) {
        Order order = orderRepository.findById(order_id)
            .orElseThrow(() -> new OrderNotFoundException("Order not found for id: " + order_id));

        // 업체 확인
        CompanyDataDto receiverData = companyService.getCompany(order.getReceiver_id());
        CompanyDataDto supplierData = companyService.getCompany(order.getSupplier_id());

        if (receiverData == null || supplierData == null) {
            throw new OrderCreationException("Receiver or Supplier not found.");
        }

        return List.of(receiverData.getHub_id(), supplierData.getHub_id());
    }

    // 주문 수정
    @Transactional
    public ResponseDto<OrderDataDto> updateOrder(UUID order_id,
        OrderRequestDto.Update orderRequestDto) {

        // 주문 조회
        Order order = orderRepository.findById(order_id)
            .orElseThrow(() -> new OrderNotFoundException("Order not found for id: " + order_id));

        // 상품 확인
        ProductDataDto productData = productService.getProduct(order.getProduct_id());
        if (productData == null) {
            throw new ProductNotFoundException(
                "Product not found for id: " + order.getProduct_id());
        }

        // 기존 수량과 수정된 수량 차이 계산
        int originalQuantity = order.getQuantity();
        int newQuantity = orderRequestDto.getQuantity();

        // 수정하려는 수량을 반영했을 때, 초기 상품 수량을 초과하는지 확인
        if (productData.getQuantity() + originalQuantity < newQuantity) {
            throw new OrderCreationException("Quantity exceeds available stock");
        }

        // 상품 수량 복구: 기존 수량을 다시 증가
        productService.reduceProductQuantity(productData.getProduct_id(), -originalQuantity);

        // 주문 수정
        order.updateOrder(orderRequestDto);
        Order updatedOrder = orderRepository.save(order);

        // 상품 수량 감소: 수정된 수량만큼 줄임
        productService.reduceProductQuantity(productData.getProduct_id(), newQuantity);

        // 응답 데이터 생성
        OrderDataDto orderDataDto = new OrderDataDto(updatedOrder);
        return new ResponseDto<>(HttpStatus.OK.value(), "주문이 수정되었습니다.", orderDataDto);
    }

    // 주문 삭제(취소)
    @Transactional
    public ResponseDto<OrderDataDto> deleteOrder(UUID order_id, String username) {
        // 주문 조회
        Order order = orderRepository.findById(order_id)
            .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        // 소프트 삭제 처리
        order.delete(username);

        // 주문 상태를 CANCELLED로 업데이트
        order.updateStatus(OrderStatus.CANCELLED);

        Order deletedOrder = orderRepository.save(order);

        // 상품 확인
        ProductDataDto productData = productService.getProduct(order.getProduct_id());
        if (productData == null) {
            throw new ProductNotFoundException(
                "Product not found for id: " + order.getProduct_id());
        }

        // 상품 수량 복구: 기존 수량을 다시 증가
        productService.reduceProductQuantity(productData.getProduct_id(), order.getQuantity());

        // 응답 데이터 생성
        OrderDataDto orderDataDto = new OrderDataDto(deletedOrder);
        return new ResponseDto<>(HttpStatus.OK.value(), "주문이 삭제되었습니다.", orderDataDto);

    }

    // 주문 단건 조회
    @Transactional(readOnly = true)
    public ResponseDto<OrderDataDto> getOrder(UUID order_id) {
        Order order = orderRepository.findById(order_id)
            .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        // 응답 데이터 생성
        OrderDataDto orderDataDto = new OrderDataDto(order);
        return new ResponseDto<>(HttpStatus.OK.value(), "주문이 조회되었습니다.", orderDataDto);
    }

    private ResponseDto<OrderDataDto> toResponseDto(HttpStatusCode statusCode, String message,
        Order order) {

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

    // 업체의 업체 담당자 Id 가져오기
    public List<Long> getCompanyManagersIdByOrderId(UUID order_id) {
        Order order = orderRepository.findById(order_id)
            .orElseThrow(() -> new OrderNotFoundException("Order not found for id: " + order_id));

        // 업체 확인
        CompanyDataDto receiverData = companyService.getCompany(order.getReceiver_id());
        CompanyDataDto supplierData = companyService.getCompany(order.getSupplier_id());

        if (receiverData == null || supplierData == null) {
            throw new OrderCreationException("Receiver or Supplier not found.");
        }

        return List.of(receiverData.getCompany_manager_id(), supplierData.getCompany_manager_id());
    }

    // 주문과 관련된 배송 담당자 ID 리스트 조회
    public List<Long> getDeliveryManagerIdsByOrderId(UUID order_id) {
        return deliveryService.getDeliveryManagerIdsByOrderId(order_id);
    }
}
