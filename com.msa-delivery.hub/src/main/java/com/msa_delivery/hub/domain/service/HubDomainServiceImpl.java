package com.msa_delivery.hub.domain.service;


import com.msa_delivery.hub.domain.model.HubRoute;
import com.msa_delivery.hub.domain.model.Hubs;
import com.msa_delivery.hub.domain.model.Location;
import com.msa_delivery.hub.domain.port.GeoCodingPort;
import com.msa_delivery.hub.domain.repository.HubRepositoryCustom;
import com.msa_delivery.hub.domain.repository.HubWriteRepository;
import com.msa_delivery.hub.domain.repository.HubReadRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HubDomainServiceImpl implements HubDomainService {

    private final HubReadRepository hubReadRepository;
    private final GeoCodingPort geoCodingPort;
    private final HubWriteRepository hubWriteRepository;
    private final HubRepositoryCustom hubRepositoryCustom;
    private static Long hubMangerId = 17L;

//    @PersistenceContext
//    private EntityManager entityManager;  // EntityManager 추가
//
//    private Long getNextHubManagerId() {
//        return (Long) entityManager
//                .createNativeQuery("SELECT NEXTVAL('hub_manager_seq')")
//                .getSingleResult();
//    }


    @Override
    public Hubs createHubs(String name, String address, String username) {


        verifyDuplicatedHub(name);

        Location location = geoCodingPort.getGeocode(address);

        Hubs hub = Hubs.builder()
                .name(name)
                .hubManagerId(hubMangerId++)
                .address(address)
                .location(location)
                .createdBy(username)
                .createdAt(LocalDateTime.now())
                .build();
        return hubWriteRepository.save(hub);
    }


    @Override
    public void verifyDuplicatedHub(String name) {
        if (hubReadRepository.existsByNameAndIsDeletedFalse(name)) {
            throw new IllegalArgumentException("이미 존재하는 허브 입니다.");
        }
    }

    @Override
    public Hubs updateHub(UUID hubId, String name, String address, String username) {
        return hubReadRepository.findByHubId(hubId).map(hubs -> {
            if (!hubs.getAddress().equals(address)) {
                Location newLocation = geoCodingPort.getGeocode(address);
                LocalDateTime updated = LocalDateTime.now();
                hubs.updateHubData(name, address, newLocation, username ,updated);
                return hubs;
            } else {
                throw new IllegalArgumentException("현재 주소와 동일한 주소 입니다.");
            }
        }).orElseThrow(() -> new IllegalArgumentException("해당 주소의 허브를 찾을 수 없습니다."));
    }



    @Override
    public void deleteHubs(UUID hubId, String userId) {
        hubReadRepository.findByHubId(hubId).orElseThrow(() -> new IllegalArgumentException("허브를 찾을 수 없습니다."));
        LocalDateTime localDateTime = LocalDateTime.now();
        hubWriteRepository.updateHubToDeleted(hubId, localDateTime, userId);
    }

    @Override
    public List<Hubs> getHubAll() {
        return hubReadRepository.findAll();
    }

    @Override
    public List<Hubs> getHubByIsDeletedFalse(UUID hubId) {
        return hubReadRepository.findAllByIsDeletedFalseAndHubIdNot(hubId);
    }

    @Override
    public Hubs getHub(UUID hubId) {
        return hubReadRepository.findByHubId(hubId)
                .orElseThrow(() -> new IllegalArgumentException("허브를 찾을 수 없습니다."));
    }



    @Override
    public Page<Hubs> searchHubs(UUID hubId, String name, String address, Long hubManagerId, Boolean isDeleted, Pageable pageable) {
        return hubRepositoryCustom.searchHubs(hubId ,name, address, hubManagerId, isDeleted, pageable);
    }
}
