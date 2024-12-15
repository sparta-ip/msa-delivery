package com.msa_delivery.hub.domain.port;

import com.msa_delivery.hub.domain.model.Location;
import com.msa_delivery.hub.domain.model.RouteInfo;

public interface NavigationPort {
    RouteInfo calculateRouteInfo(Location origin, Location destination);
}