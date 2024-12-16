package com.msa_delivery.hub.infrastructure.kakao.adapter;

import com.msa_delivery.hub.domain.model.Location;
import com.msa_delivery.hub.domain.model.RouteInfo;
import com.msa_delivery.hub.domain.port.NavigationPort;
import com.msa_delivery.hub.infrastructure.kakao.KaKaoMapClient;
import com.msa_delivery.hub.infrastructure.kakao.response.KakaoDirectionsResponse;
import com.msa_delivery.hub.infrastructure.kakao.response.Route;
import com.msa_delivery.hub.infrastructure.kakao.response.Summary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NavigationAdapter implements NavigationPort {

    private final KaKaoMapClient kaKaoMapClient;

    @Override
    public RouteInfo calculateRouteInfo(Location origin, Location destination) {

        KakaoDirectionsResponse response = kaKaoMapClient.getRouteInfo(origin, destination);

        if (response == null || response.routes().isEmpty()) {
            throw new IllegalStateException("경로를 찾을 수 없습니다.");
        }

        Route route = response.routes().get(0);
        Summary summary = route.summary();

        return new RouteInfo(
                summary.distance(),
                summary.duration()
        );
    }
}
