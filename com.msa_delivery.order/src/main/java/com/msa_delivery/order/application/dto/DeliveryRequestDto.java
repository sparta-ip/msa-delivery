package com.msa_delivery.order.application.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryRequestDto {

    private UUID order_id;
    private Long receiver_id;
    private String receiver_slack_id;
    private UUID departure_id;
    private UUID arrival_id;
    private String address;

}
