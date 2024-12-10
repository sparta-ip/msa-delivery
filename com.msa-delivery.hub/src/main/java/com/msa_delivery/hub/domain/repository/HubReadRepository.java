package com.msa_delivery.hub.domain.repository;

public interface HubReadRepository {
    boolean existsByNameAndIsDeletedFalse(String name);
}
