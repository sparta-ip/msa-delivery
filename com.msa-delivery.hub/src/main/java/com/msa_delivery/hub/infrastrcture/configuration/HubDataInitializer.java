package com.msa_delivery.hub.infrastrcture.configuration;


import com.msa_delivery.hub.domain.model.Hubs;
import com.msa_delivery.hub.domain.model.LocationVO;
import com.msa_delivery.hub.domain.repository.HubCRUDRepository;
import com.msa_delivery.hub.infrastrcture.kakao.KaKaoMapClient;
import com.msa_delivery.hub.infrastrcture.kakao.dto.KaKaoGeoResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class HubDataInitializer {


    private final HubCRUDRepository hubCRUDRepository;
    private final KaKaoMapClient kaKaoMapClient;

    @PostConstruct
    public void initHubs() {
        List<Hubs> hubs = Arrays.asList(
                createHubWithLocation("서울특별시 센터", "서울특별시 송파구 송파대로 55"),
                createHubWithLocation("경기 북부 센터", "경기도 고양시 덕양구 권율대로 570"),
                createHubWithLocation("경기 남부 센터", "경기도 이천시 덕평로 257-21"),
                createHubWithLocation("부산광역시 센터", "부산 동구 중앙대로 206"),
                createHubWithLocation("대구광역시 센터", "대구 북구 태평로 161"),
                createHubWithLocation("인천광역시 센터", "인천 남동구 정각로 29"),
                createHubWithLocation("광주광역시 센터", "광주 서구 내방로 111"),
                createHubWithLocation("대전광역시 센터", "대전 서구 둔산로 100"),
                createHubWithLocation("울산광역시 센터", "울산 남구 중앙로 201"),
                createHubWithLocation("세종특별자치시 센터", "세종특별자치시 한누리대로 2130"),
                createHubWithLocation("강원특별자치도 센터", "강원특별자치도 춘천시 중앙로 1"),
                createHubWithLocation("충청북도 센터", "충북 청주시 상당구 상당로 82"),
                createHubWithLocation("충청남도 센터", "충남 홍성군 홍북읍 충남대로 21"),
                createHubWithLocation("전북특별자치도 센터", "전북특별자치도 전주시 완산구 효자로 225"),
                createHubWithLocation("전라남도 센터", "전남 무안군 삼향읍 오룡길 1"),
                createHubWithLocation("경상북도 센터", "경북 안동시 풍천면 도청대로 455"),
                createHubWithLocation("경상남도 센터", "경남 창원시 의창구 중앙대로 300")
        );
        hubCRUDRepository.saveAll(hubs);
    }

    private Hubs createHubWithLocation(String name, String address) {
        LocationVO location = null;

        KaKaoGeoResponse geoResponse = kaKaoMapClient.convertAddressToGeocode(address);
            location = geoResponse.getDocuments().get(0).toLocation();


            return Hubs.builder()
                    .name(name)
                    .address(address)
                    .location(location)
                    .createdAt(LocalDateTime.now())
                    .createdBy("system")
                    .isDeleted(false)
                    .build();

    }

}