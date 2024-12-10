package com.msa_delivery.hub.infrastrcture.repository;

import com.msa_delivery.hub.domain.repository.HubReadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaHubReadRepositoryAdapter implements HubReadRepository {

    private final JpaHubRepositoryRead jpaHubRepositoryRead;

    @Override
    public boolean existsByNameAndIsDeletedFalse(String name) {
        return jpaHubRepositoryRead.existsByNameAndIsDeletedFalse(name);
    }
}
