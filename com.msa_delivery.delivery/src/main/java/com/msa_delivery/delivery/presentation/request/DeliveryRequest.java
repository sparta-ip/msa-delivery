package com.msa_delivery.delivery.presentation.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class DeliveryRequest {
    @NotNull(message = "주문 ID는 필수 입력 값입니다.")
//    @JsonProperty("order_id")
    private UUID orderId;

    @NotBlank(message = "수령인 ID는 필수 입력 값입니다.")
//    @JsonProperty("receiver_id")
    private Long receiverId;

    @NotBlank(message = "수령인 슬랙 ID는 필수 입력 값입니다.")
//    @JsonProperty("receiver_slack_id")
    private String receiverSlackId;

    @NotBlank(message = "출발 허브 ID는 필수 입력 값입니다.")
//    @JsonProperty("departure_id")
    private UUID departureId;

    @NotBlank(message = "도착 허브 ID는 필수 입력 값입니다.")
//    @JsonProperty("arrival_id")
    private UUID arrivalId;

    @NotBlank(message = "주소는 필수 입력 값입니다.")
    private String address;
}
