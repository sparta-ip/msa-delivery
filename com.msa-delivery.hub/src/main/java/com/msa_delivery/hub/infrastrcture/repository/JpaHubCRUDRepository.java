package com.msa_delivery.hub.infrastrcture.repository;

import com.msa_delivery.hub.domain.model.Hubs;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaHubCRUDRepository extends JpaRepository<Hubs, UUID> {


}
