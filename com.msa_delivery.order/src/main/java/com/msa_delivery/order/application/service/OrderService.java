package com.msa_delivery.order.application.service;

import com.msa_delivery.order.application.dto.CompanyDataDto;
import com.msa_delivery.order.application.dto.DeliveryResponseDto;
import com.msa_delivery.order.application.dto.HubDataDto;
import com.msa_delivery.order.application.dto.OrderDataDto;
import com.msa_delivery.order.application.dto.OrderRequestDto;
import com.msa_delivery.order.application.dto.ProductDataDto;
import com.msa_delivery.order.application.dto.ResponseDto;
import com.msa_delivery.order.application.exception.AccessDeniedException;
import com.msa_delivery.order.application.exception.InvalidInputException;
import com.msa_delivery.order.application.exception.OrderCreationException;
import com.msa_delivery.order.application.exception.OrderNotFoundException;
import com.msa_delivery.order.application.exception.ProductNotFoundException;
import com.msa_delivery.order.domain.model.Order;
import com.msa_delivery.order.domain.model.OrderStatus;
import com.msa_delivery.order.domain.repository.OrderRepository;
import com.msa_delivery.order.domain.repository.OrderRepositoryCustom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
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
    private final SlackMsgService slackMsgService;

    private final OrderRepository orderRepository;

    private final OrderRepositoryCustom orderRepositoryCustom;

    @Value("${googleai.api.key}")
    private String googleApiKey;

    // 주문 생성
    @Transactional
    public ResponseDto<OrderDataDto> createOrder(OrderRequestDto.Create orderRequestDto,
        String username) {

        log.info("Request received in OrderService at /orders createOrder");

        try {
            // 상품 확인
            ProductDataDto productData = productService.getProduct(orderRequestDto.getProduct_id());
            if (productData == null) {
                throw new ProductNotFoundException(
                    "Product not found for id: " + orderRequestDto.getProduct_id());
            }
            log.info("상품 확인 : " + productData.toString());

            // 재고 확인
            if (productData.getQuantity() < orderRequestDto.getQuantity()) {
                throw new OrderCreationException("Quantity exceeds maximum quantity");
            }

            log.info("상품 재고 확인");

            // 업체 확인
            CompanyDataDto receiverData = companyService.getCompany(
                orderRequestDto.getReceiver_id());
            CompanyDataDto supplierData = companyService.getCompany(
                orderRequestDto.getSupplier_id());
            if (receiverData == null || supplierData == null) {
                throw new OrderCreationException("Receiver or Supplier not found.");
            }

            log.info("업체 확인 (수령업체) : " + receiverData.toString());
            log.info("업체 확인 (공급업체) : " + supplierData.toString());

            // 업체 허브 정보확인
            HubDataDto receiverHubData = hubService.getHub(receiverData.getHub_id());
            HubDataDto supplierHubData = hubService.getHub(supplierData.getHub_id());
            if (receiverHubData == null || supplierHubData == null) {
                throw new OrderCreationException("Receiver or Hub not found.");
            }

            log.info("업체 허브 확인 (수령업체) : " + receiverHubData.toString());
            log.info("업체 허브 확인 (공급업체) : " + supplierHubData.toString());

            // 재고 차감
            try {
                productService.reduceProductQuantity(orderRequestDto.getProduct_id(),
                    orderRequestDto.getQuantity());
                log.info("상품 재고 차감");
            } catch (Exception e) {
                throw new OrderCreationException("재고 차감 중 오류가 발생했습니다.");
            }

            try {
                // 주문 생성 및 저장
                Order order = Order.createOrder(orderRequestDto);
                Order savedOrder = orderRepository.save(order);

                log.info("주문 저장 완료");

                // 배송 요청 및 AI 예측
                DeliveryResponseDto deliveryResponseDto = deliveryService.createDelivery(savedOrder, receiverData, supplierData);
                savedOrder.addDeliveryId(deliveryResponseDto.getDelivery().getDelivery_id());
                orderRepository.save(savedOrder); // delivery_id 업데이트 후 다시 저장

                log.info("saved order : " + savedOrder.getDelivery_id());

                log.info("배송 요청 및 AI 예측");

                String finalDeliveryTime = geminiService.predictFinalDeliveryTime(savedOrder,
                    productData, receiverData, supplierHubData, receiverHubData);
                log.info("최종 발송 시한: {}", finalDeliveryTime);

                // Slack 메시지 전송
                sendSlackMessage(savedOrder,
                    productData, receiverData, supplierHubData, receiverHubData, finalDeliveryTime);

                log.info("슬랙 메시지 전송");

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

    // 주문 수정
    @Transactional
    public ResponseDto<OrderDataDto> updateOrder(UUID order_id,
        OrderRequestDto.Update orderRequestDto) {

        // 변경사항이 없으면 예외처리
        if (orderRequestDto.getRequest() == null && orderRequestDto.getQuantity() == null) {
            throw new InvalidInputException("수정사항이 없습니다.");
        }

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
        int newQuantity = orderRequestDto.getQuantity() != null ? orderRequestDto.getQuantity() : originalQuantity;

        // 수정하려는 수량을 반영했을 때, 초기 상품 수량을 초과하는지 확인
        if (productData.getQuantity() + originalQuantity < newQuantity) {
            throw new OrderCreationException("Quantity exceeds available stock");
        }

        // 상품 수량 복구: 기존 수량을 다시 증가
        productService.reduceProductQuantity(productData.getProduct_id(), -originalQuantity);

        // 주문 수정
        order.updateOrder(orderRequestDto);

        // 상품 수량 감소: 수정된 수량만큼 줄임
        productService.reduceProductQuantity(productData.getProduct_id(), newQuantity);

        // 응답 데이터 생성
        OrderDataDto orderDataDto = new OrderDataDto(order);
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

        // 상품 확인
        ProductDataDto productData = productService.getProduct(order.getProduct_id());
        if (productData == null) {
            throw new ProductNotFoundException(
                "Product not found for id: " + order.getProduct_id());
        }

        // 상품 수량 복구: 기존 수량을 다시 증가
        productService.reduceProductQuantity(productData.getProduct_id(), order.getQuantity());

        // 응답 데이터 생성
        OrderDataDto orderDataDto = new OrderDataDto(order);
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

    // 주문 전체 조회 및 검색
    @Transactional(readOnly = true)
    public ResponseDto<Page<OrderDataDto>> getAllOrders(int page, int size, String sortBy,
        String direction, String search, String user_id, String username, String role) {
        // 페이지네이션 및 정렬 설정
        PageRequest pageRequest = PageRequest.of(
            page,
            size,
            Sort.by(Sort.Direction.fromString(direction), sortBy)
        );

        // 주문 전체 조회 (검색 조건 포함)
        Page<Order> orderPage = orderRepositoryCustom.findOrdersWithSearch(pageRequest, search);

        // 주문 리스트 필터링 (권한 검증)
        List<Order> filteredOrders = orderPage.stream()
            .filter(order -> {
                try {
                    return checkUserRoleForOrder(order, user_id, role);
                } catch (AccessDeniedException e) {
                    return false; // 권한이 없으면 해당 주문 제외
                }
            })
            .collect(Collectors.toList());

        // 필터링된 주문들을 DTO로 변환
        Page<OrderDataDto> orderDataDtos = new PageImpl<>(filteredOrders.stream()
            .map(OrderDataDto::new)
            .collect(Collectors.toList()), pageRequest, orderPage.getTotalElements());

        return new ResponseDto<>(HttpStatus.OK.value(), "조회가 완료되었습니다.", orderDataDtos);
    }

    // 주문 전체 조회 및 검색에서 권한 검증 로직
    private boolean checkUserRoleForOrder(Order order, String user_id, String role)
        throws AccessDeniedException {
        try {
            Long userId = Long.parseLong(user_id); // String -> Long 변환

            // 1. 마스터 관리자 권한이 있는 경우 통과
            if ("MASTER".equals(role)) {
                return true;
            }

            // 2. 허브 관리자인 경우, 해당 주문의 허브와 연결된 사용자인지 검증
            if ("HUB_MANAGER".equals(role)) {
                List<UUID> hubIdsOfOrder = getHubIdByOrderId(order.getOrder_id());
                boolean hasAccess = hubIdsOfOrder.stream().anyMatch(hub_id -> {
                    try {
                        Long hubManagerId = hubService.getHub(hub_id).getHub_manager_id();
                        return hubManagerId != null && hubManagerId.equals(userId);
                    } catch (Exception e) {
                        throw new AccessDeniedException("허브 관리자 권한이 없습니다.");
                    }
                });
                if (hasAccess) {
                    return true;
                }
            }

            // 3. 업체 담당자는 본인 주문만 조회 가능
            if ("COMPANY_MANAGER".equals(role)) {
                List<Long> companyManagersId = getCompanyManagersIdByOrderId(order.getOrder_id());
                if (companyManagersId.contains(userId)) {
                    return true;
                }
            }

            // 4. 배송 담당자는 본인 주문만 조회 가능
            if ("DELIVERY_MANAGER".equals(role)) {
                List<Long> deliveryManagerIds = getDeliveryManagerIdsByOrderId(order.getOrder_id());
                if (deliveryManagerIds.contains(userId)) {
                    return true;
                }
            }

            // 권한이 없으면 false 반환
            return false;
        } catch (Exception e) {
            throw new AccessDeniedException("권한이 없습니다.");
        }
    }

    // 슬랙메시지에 전달한 발송시한 값 추출하기
    public void sendSlackMessage(Order order, ProductDataDto productData,
        CompanyDataDto receiverData, HubDataDto supplierHubData, HubDataDto receiverHubData,
        String finalDeliveryTime) {
        try {
            log.info("sendSlackMessage order: " + order.getDelivery_id());

            // 날짜 포맷 검증 및 파싱
            LocalDateTime finalDeliveryDateTime = null;

            // 정규 표현식으로 날짜 패턴 추출
            Pattern pattern = Pattern.compile("(\\d{2})월 (\\d{2})일 (\\d{2}):(\\d{2})");
            Matcher matcher = pattern.matcher(finalDeliveryTime);

            if (matcher.find()) {
                // 날짜 정보가 있을 경우만 파싱
                String month = matcher.group(1);
                String day = matcher.group(2);
                String hour = matcher.group(3);
                String minute = matcher.group(4);

                // 현재 연도 가져오기
                int currentYear = LocalDate.now().getYear();

                // LocalDateTime 구성
                finalDeliveryDateTime = LocalDateTime.of(
                    currentYear, Integer.parseInt(month), Integer.parseInt(day),
                    Integer.parseInt(hour), Integer.parseInt(minute)
                );
            } else {
                log.warn("유효한 날짜 형식이 감지되지 않았습니다: {}", finalDeliveryTime);
                finalDeliveryDateTime = LocalDateTime.now().plusDays(1).withHour(9).withMinute(0);
            }

            // Null 체크 추가
            String orderId = (order != null && order.getOrder_id() != null) ? String.valueOf(
                order.getOrder_id()) : "N/A";
            String slackId = (receiverData != null && receiverData.getSlack_id() != null) ? receiverData.getSlack_id() : "N/A";
            String productName = (productData != null && productData.getName() != null) ? productData.getName() : "N/A";
            String request = (order != null && order.getRequest() != null) ? order.getRequest() : "없음";
            String supplierHubName = (supplierHubData != null && supplierHubData.getHub_name() != null) ? supplierHubData.getHub_name() : "N/A";
            String receiverHubName = (receiverHubData != null && receiverHubData.getHub_name() != null) ? receiverHubData.getHub_name() : "N/A";
            String receiverAddress = (receiverData != null && receiverData.getAddress() != null) ? receiverData.getAddress() : "N/A";
            String deliveryId = (order != null && order.getDelivery_id() != null) ? String.valueOf(
                order.getDelivery_id()) : "N/A";

            log.info("orderId: " + orderId + ", slackId: " + slackId + ", productName: " + productName + ", request: " + request + ", supplierHubName: " + supplierHubName + ", receiverHubName: " + receiverHubName + ", deliveryId: " + deliveryId);

            String slackMessage = String.format(
                "**주문 번호**: %s\n" +
                    "**주문자 정보**: %s\n" +
                    "**상품 정보**: %s\n" +
                    "**요청 사항**: %s\n" +
                    "**발송지**: %s\n" +
                    "**경유지**: %s\n" +
                    "**도착지**: %s\n" +
                    "**배송 ID**: %s\n\n" +
                    "위 내용을 기반으로 도출된 최종 발송 시한은 %s 입니다.",
                orderId,
                slackId,
                productName + " " + (order != null ? order.getQuantity() : "0") + "박스", // quantity도 체크
                request + "까지 보내주세요!",
                supplierHubName,
                receiverHubName,
                receiverAddress,
                deliveryId,
                finalDeliveryDateTime.format(DateTimeFormatter.ofPattern("MM월 dd일 HH:mm"))
            );

            // Slack 메시지 전송 (SlackMsg 클라이언트 호출)
            slackMsgService.createSlackMsg(receiverData.getCompany_manager_id(),
                receiverData.getSlack_id(), finalDeliveryDateTime, slackMessage);

            log.info("Slack 메시지를 전송했습니다: {}", finalDeliveryDateTime);
        } catch (Exception e) {
            log.error("Slack 메시지 전송 중 오류 발생: {}", e.getMessage());
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

    // 업체의 업체 담당자 Id 가져오기
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public List<Long> getDeliveryManagerIdsByOrderId(UUID order_id) {
        return deliveryService.getDeliveryManagerIdsByOrderId(order_id);
    }

}
