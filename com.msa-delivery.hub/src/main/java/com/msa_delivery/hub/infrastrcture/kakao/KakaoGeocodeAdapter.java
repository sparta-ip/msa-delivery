package com.msa_delivery.hub.infrastrcture.kakao;

import com.msa_delivery.hub.domain.model.Location;
import com.msa_delivery.hub.domain.port.GeoCodingPort;
import com.msa_delivery.hub.infrastrcture.kakao.response.KaKaoGeoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KakaoGeocodeAdapter implements GeoCodingPort {

    private final KaKaoMapClient kakaoMapClient;
    @Override
    public Location getGeocode(String address) {
        KaKaoGeoResponse geoResponse = kakaoMapClient.convertAddressToGeocode(address);
        if (geoResponse.getDocuments().isEmpty()) {
            throw new IllegalArgumentException("주소를 찾을 수 없습니다.");
        }
        return geoResponse.getDocuments().get(0).toLocation();
    }
}
