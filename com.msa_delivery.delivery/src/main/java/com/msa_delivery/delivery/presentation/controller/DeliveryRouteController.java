package com.msa_delivery.delivery.presentation.controller;

import com.msa_delivery.delivery.application.service.DeliveryRouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/delivery-managers")
@RequiredArgsConstructor
public class DeliveryRouteController {

    private final DeliveryRouteService deliveryRouteService;
}
