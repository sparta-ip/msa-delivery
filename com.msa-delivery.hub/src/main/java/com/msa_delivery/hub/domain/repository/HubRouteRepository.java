package com.msa_delivery.hub.domain.repository;

import com.msa_delivery.hub.domain.model.HubRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HubRouteRepository extends JpaRepository<HubRoute, UUID> {

    @Query("SELECT r FROM HubRoute r WHERE r.arrivalHub.hubId = :arrivalHubId OR r.departureHub.hubId = :departureHubId")
    List<HubRoute> findAllByArrivalHubIdOrDepartureHubId(
            @Param("arrivalHubId") UUID arrivalHubId,
            @Param("departureHubId") UUID departureHubId
    );

    @Modifying
    @Query("""
        UPDATE HubRoute r
        SET r.isDeleted = true,
            r.deletedAt = :now,
            r.deletedBy = :userId
        WHERE (r.arrivalHub.hubId = :hubId OR r.departureHub.hubId = :hubId)
        AND r.isDeleted = false
    """)
    void softDeleteByHubId(@Param("hubId") UUID hubId, @Param("userId") String userId, @Param("now") LocalDateTime now);

}
