package com.msa_delivery.order.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public class DeliveryResponseDto {
    private DeliveryDto delivery;
    private List<DeliveryRouteDto> deliveryRoutes;

    @Data
    public static class DeliveryDto {

//        @JsonProperty("deliveryId")
        private UUID delivery_id;

//        @JsonProperty("orderId")
        private UUID order_id;

//        @JsonProperty("deliveryManagerId")
        private UUID delivery_manager_id;

//        @JsonProperty("receiverId")
        private Long receiver_id;

//        @JsonProperty("receiverSlackId")
        private String receiver_slack_id;

//        @JsonProperty("departureId")
        private UUID departure_id;

//        @JsonProperty("arrivalId")
        private UUID arrival_id;

        private String address;

//        @JsonProperty("deliveryStatus")
        private String delivery_status;

//        @JsonProperty("createdAt")
        private LocalDateTime created_at;

//        @JsonProperty("createdBy")
        private String created_by;
    }

    @Data
    public static class DeliveryRouteDto {

//        @JsonProperty("deliveryRouteId")
        private UUID delivery_route_id;

//        @JsonProperty("deliveryId")
        private UUID delivery_id;

//        @JsonProperty("deliveryManagerId")
        private Long delivery_manager_id;

        private Integer sequence;

//        @JsonProperty("departureId")
        private UUID departure_id;

//        @JsonProperty("arrivalId")
        private UUID arrival_id;

//        @JsonProperty("expectDistance")
        private Integer expect_distance;

//        @JsonProperty("expectDuration")
        private Integer  expect_duration;

//        @JsonProperty("deliveryStatus")
        private String delivery_status;

//        @JsonProperty("createdAt")
        private LocalDateTime created_at;

//        @JsonProperty("createdBy")
        private String created_by;
    }
}
