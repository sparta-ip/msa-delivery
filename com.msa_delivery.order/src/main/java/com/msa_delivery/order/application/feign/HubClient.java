package com.msa_delivery.order.application.feign;

import com.msa_delivery.order.application.dto.CompanyDataDto;
import com.msa_delivery.order.application.dto.HubDataDto;
import com.msa_delivery.order.application.dto.ResponseDto;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="hub-service")
public interface HubClient {

    @GetMapping("/api/hubs/{hub_id}")
    ResponseDto<HubDataDto> getHub(@PathVariable UUID hub_id);

}
