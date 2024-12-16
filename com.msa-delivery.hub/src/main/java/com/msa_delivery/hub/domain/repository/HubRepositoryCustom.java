package com.msa_delivery.hub.domain.repository;

import com.msa_delivery.hub.domain.model.Hubs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface HubRepositoryCustom {

    Page<Hubs> searchHubs(UUID hubId, String name, String address, Long hubManagerId, Boolean isDeleted, Pageable pageable);
}
