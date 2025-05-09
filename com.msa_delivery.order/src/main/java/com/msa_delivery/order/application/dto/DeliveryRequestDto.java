package com.msa_delivery.order.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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

//    @JsonProperty("orderId")
    private UUID order_id;

//    @JsonProperty("receiverId")
    private Long receiver_id;

//    @JsonProperty("receiverSlackId")
    private String receiver_slack_id;

//    @JsonProperty("departureId")
    private UUID departure_id;

//    @JsonProperty("arrivalId")
    private UUID arrival_id;

    private String address;

}
