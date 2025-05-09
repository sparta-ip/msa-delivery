package com.msa_delivery.order.application.service;

import com.msa_delivery.order.application.dto.CompanyDataDto;
import com.msa_delivery.order.application.dto.ResponseDto;
import com.msa_delivery.order.application.feign.CompanyClient;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyClient companyClient;

    // 업체 정보 조회
    public CompanyDataDto getCompany(UUID company_id) {
        ResponseDto<CompanyDataDto> response = companyClient.getCompany(company_id);
        if (response.getData() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "존재하지 않는 업체입니다.");
        }
        return response.getData();
    }
}

