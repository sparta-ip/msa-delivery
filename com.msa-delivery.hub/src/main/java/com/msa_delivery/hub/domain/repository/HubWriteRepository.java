package com.msa_delivery.hub.domain.repository;


import com.msa_delivery.hub.domain.model.Hubs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface HubWriteRepository extends JpaRepository<Hubs, UUID> {

    @Modifying
    @Query("UPDATE Hubs h SET h.deletedAt = :deletedAt, h.isDeleted = true, h.deletedBy = :deletedBy WHERE h.hubId = :hubId")
    void updateHubToDeleted(@Param("hubId") UUID hubId, @Param("deletedAt") LocalDateTime deletedAt, @Param("deletedBy") String deletedBy);
}


