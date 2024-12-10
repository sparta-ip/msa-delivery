package com.msa_delivery.order.presentation.controller;

import com.msa_delivery.order.application.dto.OrderDataDto;
import com.msa_delivery.order.application.dto.OrderRequestDto;
import com.msa_delivery.order.application.dto.ResponseDto;
import com.msa_delivery.order.application.service.OrderService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 주문 생성
    @PostMapping
    public ResponseDto<OrderDataDto> createOrder(
        @RequestBody OrderRequestDto.Create orderRequestDto,
        @RequestHeader(value = "X-User_Id", required = true) String user_id,
        @RequestHeader(value = "X-Username", required = true) String username,
        @RequestHeader(value = "X-Role", required = true) String role) {

        return orderService.createOrder(orderRequestDto,username);
    }

    // 주문 수정
    @PutMapping("/{order_id}")
    public ResponseDto<OrderDataDto> updateOrder(
        @RequestBody OrderRequestDto.Update orderRequestDto,
        @RequestHeader(value = "X-User_Id", required = true) String user_id,
        @RequestHeader(value = "X-Username", required = true) String username,
        @RequestHeader(value = "X-Role", required = true) String role,
        @PathVariable UUID order_id
    ) {
        return orderService.updateOrder(order_id, orderRequestDto);
    }

    // 주문 삭제(취소)
    @DeleteMapping("/{order_id}")
    public ResponseDto<OrderDataDto> deleteOrder(
        @RequestHeader(value = "X-User_Id", required = true) String user_id,
        @RequestHeader(value = "X-Username", required = true) String username,
        @RequestHeader(value = "X-Role", required = true) String role,
        @PathVariable UUID order_id
    ) {
        return orderService.deleteOrder(order_id, username);
    }
}
