package com.msa_delivery.order.application.feign;

import com.msa_delivery.order.application.dto.CompanyDataDto;
import com.msa_delivery.order.application.dto.ProductDataDto;
import com.msa_delivery.order.application.dto.ResponseDto;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="company-service")
public interface CompanyClient {

    @GetMapping("/api/companies/{company_id}")
    ResponseDto<CompanyDataDto> getCompany(@PathVariable UUID company_id);

    @GetMapping("/api/products/{product_id}")
    ResponseDto<ProductDataDto> getProduct(@PathVariable UUID product_id);

    @GetMapping("/api/products/{product_id}/reduceQuantity")
    void reduceProductQuantity(@PathVariable("product_id") UUID product_id, @RequestParam("quantity") int quantity);

}
