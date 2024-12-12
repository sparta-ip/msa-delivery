package com.msa_delivery.delivery.domain.repository;

import com.msa_delivery.delivery.domain.model.DeliveryManager;
import com.msa_delivery.delivery.domain.model.DeliveryManagerType;
import com.msa_delivery.delivery.infrastructure.repository.JpaDeliveryManagerRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeliveryManagerRepository  extends JpaRepository<DeliveryManager, Long>, JpaDeliveryManagerRepository {
    Optional<DeliveryManager> findFirstByDeliveryManagerTypeAndOrderIdIsNullAndIsDeleteFalseOrderBySequenceAsc(DeliveryManagerType deliveryManagerType);
}
