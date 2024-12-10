package com.msa_delivery.hub.domain.repository;


import com.msa_delivery.hub.domain.model.Hubs;

import java.util.List;

public interface HubCRUDRepository {

    List<Hubs> saveAll(List<Hubs> hubs);
    Hubs save(Hubs hubs);

}


