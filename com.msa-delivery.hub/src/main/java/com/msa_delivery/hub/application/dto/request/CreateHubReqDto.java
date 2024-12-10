package com.msa_delivery.hub.application.dto.request;

import com.msa_delivery.hub.domain.model.Hubs;
import com.msa_delivery.hub.domain.model.LocationVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class CreateHubReqDto {

    private Hub hub;


    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Hub {

        private String name;
        private String address;

    }

    public Hubs toEntity(String createdBy, LocationVO location) {
        return Hubs.builder()
                .name(hub.getName())
                .address(hub.getAddress())
                .location(location)
                .createdBy(createdBy)
                .build();
    }

}

