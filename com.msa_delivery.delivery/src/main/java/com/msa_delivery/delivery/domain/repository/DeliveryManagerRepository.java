package com.msa_delivery.delivery.domain.repository;

import com.msa_delivery.delivery.domain.model.DeliveryManager;
import com.msa_delivery.delivery.infrastructure.repository.JpaDeliveryManagerRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryManagerRepository  extends JpaRepository<DeliveryManager, Long>, JpaDeliveryManagerRepository {
}
