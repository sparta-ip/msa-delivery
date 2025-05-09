package com.msa_delivery.order.application.service;

import com.msa_delivery.order.application.dto.ProductDataDto;
import com.msa_delivery.order.application.dto.ResponseDto;
import com.msa_delivery.order.application.feign.CompanyClient;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final CompanyClient companyClient;

    // 상품 정보 조회
    public ProductDataDto getProduct(UUID product_id) {
        ResponseDto<ProductDataDto> response = companyClient.getProduct(product_id);
        if (response.getData() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "제품이 존재하지 않습니다.");
        }
        return response.getData();
    }

    // 상품 수량 감소
    public void reduceProductQuantity(UUID productId, Integer quantity) {
        companyClient.reduceProductQuantity(productId, quantity);
    }
}
