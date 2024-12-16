package com.msa_delivery.hub.infrastrcture.kakao;

import com.msa_delivery.hub.domain.model.Location;
import com.msa_delivery.hub.infrastrcture.kakao.response.KaKaoGeoResponse;
import com.msa_delivery.hub.infrastrcture.kakao.response.KakaoDirectionsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Slf4j
@Component
@RequiredArgsConstructor
public class KaKaoMapClient {

    @Value("${kakao-api-key}")
    private String kakaoApikey;
    private final RestTemplate restTemplate;
    private static final String KAKAO_MAP_API_URL = "https://dapi.kakao.com/v2/local/search/address.json";

    public KaKaoGeoResponse convertAddressToGeocode(String address) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApikey);
        headers.set("Accept", "application/json");
        URI getUri = UriComponentsBuilder.fromHttpUrl(KAKAO_MAP_API_URL)
                .queryParam("query", address).build().encode().toUri();


        HttpEntity<String> addressEntity = new HttpEntity<>(headers);
        ResponseEntity<KaKaoGeoResponse> response = restTemplate.exchange(
                getUri,
                HttpMethod.GET,
                addressEntity,
                KaKaoGeoResponse.class);
        return response.getBody();

    }

    public KakaoDirectionsResponse getRouteInfo(Location origin, Location destination) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApikey);
        headers.set("Accept", "application/json");
        URI uri = UriComponentsBuilder
                .fromHttpUrl("https://apis-navi.kakaomobility.com/v1/directions")
                .queryParam("origin", origin.getLongitude() + "," + origin.getLatitude())
                .queryParam("destination", destination.getLongitude() + "," + destination.getLatitude())
                .build()
                .toUri();
        HttpEntity<String> addressEntity = new HttpEntity<>(headers);
        ResponseEntity<KakaoDirectionsResponse> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                addressEntity,
                KakaoDirectionsResponse.class);
        return response.getBody();



    }


}
