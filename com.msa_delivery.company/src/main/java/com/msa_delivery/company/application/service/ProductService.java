package com.msa_delivery.company.application.service;

import com.msa_delivery.company.application.dto.ProductDto;
import com.msa_delivery.company.domain.model.Company;
import com.msa_delivery.company.domain.model.Product;
import com.msa_delivery.company.domain.repository.CompanyRepository;
import com.msa_delivery.company.domain.repository.ProductRepository;
import com.msa_delivery.company.infrastructure.client.HubClient;
import com.msa_delivery.company.infrastructure.client.HubDto;
import com.msa_delivery.company.presentation.request.ProductRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CompanyRepository companyRepository;
    private final HubClient hubClient;

    public ProductDto createProduct(ProductRequest request, Long userId, String username, String role) throws AccessDeniedException {
        // 업체, 허브 ID 확인
        UUID companyId = request.getCompanyId();
        Company company = companyRepository.findByIdAndIsDeleteFalse(companyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 업체를 찾을 수 없습니다."));
        UUID hubId = company.getHubId();
        // 권한 체크
        checkCreateRole(role, userId, hubId, company.getManagerId());

        String name = request.getName();
        Integer price = request.getPrice();
        Integer quantity = request.getQuantity();

        // 상품 생성
        Product product = Product.create(company, hubId, name, price, quantity);
        product.setCreatedBy(username);
        productRepository.save(product);
        return ProductDto.create(product);
    }

    private void checkCreateRole(String role, Long userId, UUID companyHubId, Long companyManagerId) throws AccessDeniedException {
        switch (role) {
            case "MASTER":
                // MASTER 는 모든 작업 가능, 권한 검증 필요 없음
                break;

            case "HUB_MANAGER":
                // HubClient 를 사용하여 companyHubId를 기반으로 허브 정보를 조회
                HubDto hub = hubClient.getHubById(companyHubId).getBody().getData();
                Long hubManagerId = hub.getHubManagerId();

                // 허브 ID에 매핑된 허브 관리자 ID와 현재 userId 비교
                if (!userId.equals(hubManagerId)) {
                    throw new AccessDeniedException("해당 업체의 상품을 생성할 권한이 없습니다.");
                }
                break;

            case "COMPANY_MANAGER":
                // 요청 헤더의 userId와 매핑된 companyManagerId 비교
                if (!userId.equals(companyManagerId)) {
                    throw new AccessDeniedException("본인의 업체의 상품만 생성할 수 있습니다");
                }
            case "DELIVERY_MANAGER":
                // 배송 담당자와 업체 담당자는 생성 권한 없음
                throw new AccessDeniedException("해당 권한으로는 상품을 생성할 수 없습니다.");

            default:
                throw new IllegalArgumentException("유효하지 않은 권한입니다.");
        }
    }

}
