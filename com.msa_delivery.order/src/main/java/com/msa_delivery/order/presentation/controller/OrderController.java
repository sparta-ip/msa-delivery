package com.msa_delivery.order.presentation.controller;

import com.msa_delivery.order.application.dto.OrderDataDto;
import com.msa_delivery.order.application.dto.OrderRequestDto;
import com.msa_delivery.order.application.dto.ResponseDto;
import com.msa_delivery.order.application.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseDto<OrderDataDto> createOrder(
        @RequestBody OrderRequestDto.Create orderRequestDto,
        @RequestHeader(value = "X-Username", required = true) String username,
        @RequestHeader(value = "X-Role", required = true) String role) {

        return orderService.createOrder(orderRequestDto,username);
    }
}
