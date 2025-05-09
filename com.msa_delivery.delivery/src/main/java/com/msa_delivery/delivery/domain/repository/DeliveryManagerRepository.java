package com.msa_delivery.delivery.domain.repository;

import com.msa_delivery.delivery.domain.model.DeliveryManager;
import com.msa_delivery.delivery.domain.model.DeliveryManagerType;
import com.msa_delivery.delivery.infrastructure.repository.JpaDeliveryManagerRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DeliveryManagerRepository  extends JpaRepository<DeliveryManager, Long>, JpaDeliveryManagerRepository {
    Optional<DeliveryManager> findByIdAndIsDeleteFalse(Long deliveryManagerId);

    @Query("SELECT MAX(dm.sequence) FROM DeliveryManager dm WHERE dm.type = :type AND dm.isDelete = false")
    Integer findMaxSequenceByType(@Param("type") DeliveryManagerType type);

    @Modifying
    @Query("UPDATE DeliveryManager dm SET dm.sequence = dm.sequence + 1 WHERE dm.type = :type AND dm.sequence >= :sequence AND dm.isDelete = false")
    void incrementSequence(@Param("type") DeliveryManagerType type, @Param("sequence") Integer sequence);

}
