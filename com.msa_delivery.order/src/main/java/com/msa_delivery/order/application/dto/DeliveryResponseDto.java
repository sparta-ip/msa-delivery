package com.msa_delivery.order.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;

@Data
public class DeliveryResponseDto {
    private DeliveryDto delivery;
    private DeliveryRouteDto deliveryRoutes;

    @Data
    public static class DeliveryDto {

        private UUID delivery_id;
        private UUID order_id;
        private Long delivery_manager_id;
        private Long receiver_id;
        private String receiver_slack_id;
        private UUID departure_id;
        private UUID arrival_id;
        private String address;
        private String delivery_status;
        private LocalDateTime created_at;
        private String created_by;

    }

    @Data
    public static class DeliveryRouteDto {

        private UUID delivery_route_id;
        private UUID delivery_id;
        private Long delivery_manager_id;
        private UUID departure_id;
        private UUID arrival_id;
        private Integer expect_distance;
        private Integer  expect_duration;
        private String delivery_status;
        private LocalDateTime created_at;
        private String created_by;

    }
}
