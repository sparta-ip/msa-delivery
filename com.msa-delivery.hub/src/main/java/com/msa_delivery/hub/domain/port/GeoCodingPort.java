package com.msa_delivery.hub.domain.port;

import com.msa_delivery.hub.domain.model.Location;

public interface GeoCodingPort {

    Location getGeocode(String address);
}
