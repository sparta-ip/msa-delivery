package com.msa_delivery.hub.domain.service;


import com.msa_delivery.hub.domain.repository.HubReadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HubDomainServiceImpl implements HubDomainService {

    private final HubReadRepository hubReadRepository;

    @Override
    public void verifyDuplicatedHub(String name) {
        if (hubReadRepository.existsByNameAndIsDeletedFalse(name)) {
            throw new IllegalArgumentException("이미 존재하는 허브 입니다.");
        }
    }
}
