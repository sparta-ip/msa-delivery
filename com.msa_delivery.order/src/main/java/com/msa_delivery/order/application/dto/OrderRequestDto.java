package com.msa_delivery.order.application.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
public class OrderRequestDto {

    @Getter
    @Builder
    @NoArgsConstructor
    public static class Create {

        @NotNull(message = "수령업체 ID를 입력해주세요.")
        private UUID receiver_id;

        @NotNull(message = "공급업체 ID를 입력해주세요.")
        private UUID supplier_id;

        @NotNull(message = "상품 ID를 입력해주세요.")
        private UUID product_id;

        @NotNull(message = "상품 수량을 입력해주세요.")
        private Integer quantity;

        @NotNull(message = "납품 기한 일자 및 시간을 입력해주세요.")
        private String request;

        public Create(UUID receiver_id, UUID supplier_id, UUID product_id, Integer quantity, String request) {
            this.receiver_id = receiver_id;
            this.supplier_id = supplier_id;
            this.product_id = product_id;
            this.quantity = quantity;
            this.request = request;
        }

    }

    @Getter
    @Builder
    @NoArgsConstructor
    public static class Update {

        private Integer quantity;

        private String request;

        public Update(Integer quantity, String request) {
            this.quantity = quantity;
            this.request = request;
        }

    }
}
