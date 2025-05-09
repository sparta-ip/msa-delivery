package com.msa_delivery.order.application.feign;

import com.msa_delivery.order.application.dto.HubDataDto;
import com.msa_delivery.order.application.dto.ResponseDto;
import com.msa_delivery.order.infrastructure.configuration.FeignClientConfig;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "hub-service", configuration = FeignClientConfig.class)
public interface HubClient {

    @GetMapping("/api/hubs/{hub_id}")
    ResponseDto<HubDataDto> getHub(@PathVariable UUID hub_id);

}
