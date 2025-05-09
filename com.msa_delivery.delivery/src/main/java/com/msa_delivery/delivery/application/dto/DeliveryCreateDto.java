package com.msa_delivery.delivery.application.dto;

import com.msa_delivery.delivery.domain.model.Delivery;
import com.msa_delivery.delivery.domain.model.DeliveryRoute;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DeliveryCreateDto {
    private final DeliveryDto delivery;
    private final DeliveryRouteDto deliveryRoute;

    public static DeliveryCreateDto create(Delivery delivery, DeliveryRoute deliveryRoute) {
        return new DeliveryCreateDto(
                DeliveryDto.create(delivery),
                DeliveryRouteDto.create(deliveryRoute)
        );
    }
}
