package com.msa_delivery.hub.domain.repository;


import com.msa_delivery.hub.domain.model.Hubs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HubReadRepository extends JpaRepository<Hubs , UUID> {

    boolean existsByNameAndIsDeletedFalse(String name);
    Optional<Hubs> findByHubId(UUID hubId);
    @Query("SELECT h FROM Hubs h WHERE h.isDeleted = false AND h.hubId != :hubId")
    List<Hubs> findAllByIsDeletedFalseAndHubIdNot(@Param("hubId") UUID hubId);

}
