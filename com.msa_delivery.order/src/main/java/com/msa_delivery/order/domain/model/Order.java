package com.msa_delivery.order.domain.model;

import com.msa_delivery.order.application.dto.OrderRequestDto.Create;
import com.msa_delivery.order.application.dto.OrderRequestDto.Update;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PROTECTED)
@Entity
@Table(name= "p_orders", schema = "\"order\"")
@Where(clause = "is_deleted = false")
public class Order extends BaseEntity {

    @Id
    private UUID order_id;

    @Column
    private UUID delivery_id;

    @Column(nullable = false)
    private UUID receiver_id;

    @Column(nullable = false)
    private UUID supplier_id;

    @Column(nullable = false)
    private UUID product_id;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private String request;

    @Column(nullable = false, length = 50)
    private String status;

    public static Order createOrder(Create orderRequestDto) {

        return Order.builder()
            .order_id(UUID.randomUUID())
            .receiver_id(orderRequestDto.getReceiver_id())
            .supplier_id(orderRequestDto.getSupplier_id())
            .product_id(orderRequestDto.getProduct_id())
            .quantity(orderRequestDto.getQuantity())
            .request(orderRequestDto.getRequest())
            .status(String.valueOf(OrderStatus.SUCCESS))
            .build();

    }

    public void addDeliveryId(UUID delivery_id) {
        this.delivery_id = delivery_id;
    }

    public void updateOrder(Update orderRequestDto) {
        if (orderRequestDto.getQuantity() != null) {
            this.quantity = orderRequestDto.getQuantity();
        }
        if (orderRequestDto.getRequest() != null) {
            this.request = orderRequestDto.getRequest();
        }
    }

    public void updateStatus(OrderStatus status) {
        this.status = status.name();
    }
}
