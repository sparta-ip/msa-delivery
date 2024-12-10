package com.msa_delivery.hub.application.dto.response;

import com.msa_delivery.hub.domain.model.Hubs;
import com.msa_delivery.hub.domain.model.LocationVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateHubResDto {
    private UUID hubId;
    private String hubName;
    private String address;
    private LocationVO location;


    public static CreateHubResDto of(Hubs hubs) {
        return new CreateHubResDto(hubs.getHubId(), hubs.getName(), hubs.getAddress(), hubs.getLocation());
    }
}
