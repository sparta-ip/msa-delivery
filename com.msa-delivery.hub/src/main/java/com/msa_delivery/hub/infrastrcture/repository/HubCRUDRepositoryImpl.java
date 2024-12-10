package com.msa_delivery.hub.infrastrcture.repository;

import com.msa_delivery.hub.domain.model.Hubs;
import com.msa_delivery.hub.domain.repository.HubCRUDRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HubCRUDRepositoryImpl implements HubCRUDRepository {

    private final JpaHubCRUDRepository JpaHubCRUDRepository;

    @Override
    public List<Hubs> saveAll(List<Hubs> hubs) {
        return JpaHubCRUDRepository.saveAll(hubs);
    }
    @Override
    public Hubs save(Hubs hubs) {
        return JpaHubCRUDRepository.save(hubs);
    }


}
