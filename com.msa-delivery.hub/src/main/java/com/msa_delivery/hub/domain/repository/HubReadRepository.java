package com.msa_delivery.hub.domain.repository;


import com.msa_delivery.hub.domain.model.Hubs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface HubReadRepository extends JpaRepository<Hubs , UUID> {

    boolean existsByNameAndIsDeletedFalse(String name);
    Optional<Hubs> findByHubId(UUID hubId);


}
