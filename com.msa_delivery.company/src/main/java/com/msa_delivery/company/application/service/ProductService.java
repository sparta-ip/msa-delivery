package com.msa_delivery.company.application.service;

import com.msa_delivery.company.application.dto.ProductDto;
import com.msa_delivery.company.domain.model.Company;
import com.msa_delivery.company.domain.model.Product;
import com.msa_delivery.company.domain.repository.CompanyRepository;
import com.msa_delivery.company.domain.repository.ProductRepository;
import com.msa_delivery.company.infrastructure.client.HubClient;
import com.msa_delivery.company.infrastructure.client.HubDto;
import com.msa_delivery.company.presentation.request.ProductRequest;
import com.msa_delivery.company.presentation.request.ProductUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CompanyRepository companyRepository;
    private final HubClient hubClient;

    @Transactional
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

    @Transactional
    public ProductDto updateProduct(UUID productId, ProductUpdateRequest request, Long userId, String username, String role) throws AccessDeniedException {
        // 상품 확인
        Product product = productRepository.findByIdAndIsDeleteFalse(productId)
                        .orElseThrow(() -> new IllegalArgumentException("해당 상품을 찾을 수 없습니다."));
        Company company = product.getCompany();
        Long managerId = company.getManagerId();
        UUID hubId = product.getHubId();

        // 권한 확인
        checkUpdateRole(role, userId, hubId, managerId);

        // 업체, 허브 업데이트
        UUID companyId = request.getCompanyId() != null ? request.getCompanyId() : product.getCompany().getId();
        if (request.getCompanyId() != null) {
            // 업체, 허브 ID 확인
            company = companyRepository.findByIdAndIsDeleteFalse(request.getCompanyId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 업체를 찾을 수 없습니다."));
            hubId = company.getHubId();
            // 권한 확인
            checkCreateRole(role, userId, hubId, managerId);
        }

        String name = request.getName() != null ? request.getName() : product.getName();
        Integer price = request.getPrice() != null ? request.getPrice() : product.getPrice();
        Integer quantity = request.getQuantity() != null ? request.getQuantity() : product.getQuantity();

        // 상품 업데이트
        product.update(company, hubId, name, price, quantity);
        product.setUpdatedBy(username);
        return ProductDto.create(product);
    }

    @Transactional
    public void deleteProduct(UUID productId, Long userId, String username, String role) throws AccessDeniedException {
        // 상품 확인
        Product product = productRepository.findByIdAndIsDeleteFalse(productId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품을 찾을 수 없습니다."));
        UUID hubId = product.getHubId();

        // 권한 확인
        checkDeleteRole(role, userId, hubId);

        product.delete(username);
    }

    @Transactional(readOnly = true)
    public ProductDto getProductById(UUID productId, Long userId, String role) throws AccessDeniedException {
        // 상품 확인
        Product product = productRepository.findByIdAndIsDeleteFalse(productId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품을 찾을 수 없습니다."));

        // 권한 확인
        UUID hubId = product.getHubId();
        checkReadyRole(role, userId, hubId);

        return ProductDto.create(product);
    }

    @Transactional(readOnly = true)
    public Page<ProductDto> getProducts(String search, UUID hubId, Integer minPrice, Integer maxPrice,
                                        Integer minQuantity, Integer maxQuantity, Long userId, String role, Pageable pageable) throws AccessDeniedException {

        if (role.equals("HUB_MANAGER")) {
            if (hubId == null) {
                throw new IllegalArgumentException("허브 담당자는 허브 ID 가 필요합니다.");
            }
            // HubClient 를 사용하여 companyHubId를 기반으로 허브 정보를 조회
            HubDto hub = hubClient.getHubById(hubId).getBody().getData();
            Long hubManagerId = hub.getHubManagerId();

            // 허브 ID에 매핑된 허브 관리자 ID와 현재 userId 비교
            if (!userId.equals(hubManagerId)) {
                throw new AccessDeniedException("해당 허브 상품에 대한 권한이 없습니다.");
            }
            // 허브 ID로 필터링 추가
            return productRepository.searchProducts(search, minPrice, maxPrice, minQuantity, maxQuantity, pageable, hubId);
        }

        // 다른 역할(MASTER, COMPANY_MANAGER, DELIVERY_MANAGER)은 모든 데이터를 검색 가능
        return productRepository.searchProducts(search, minPrice, maxPrice, minQuantity, maxQuantity, pageable, null);
    }

    @Transactional
    public void reduceProductQuantity(UUID productId, int quantity) {
        Product product = productRepository.findByIdAndIsDeleteFalse(productId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품을 찾을 수 없습니다."));
        if (product.getQuantity() < quantity) {
            throw new IllegalArgumentException("수량이 부족합니다. 주문 가능 수량 : " + product.getQuantity());
        }
        product.reduceQuantity(quantity);
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

    private void checkUpdateRole(String role, Long userId, UUID companyHubId, Long companyManagerId) throws AccessDeniedException {
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
                    throw new AccessDeniedException("해당 업체의 상품을 수정할 권한이 없습니다.");
                }
                break;

            case "COMPANY_MANAGER":
                // 요청 헤더의 userId와 매핑된 companyManagerId 비교
                if (!userId.equals(companyManagerId)) {
                    throw new AccessDeniedException("본인의 업체의 상품만 수정할 수 있습니다");
                }
            case "DELIVERY_MANAGER":
                // 배송 담당자와 업체 담당자는 생성 권한 없음
                throw new AccessDeniedException("해당 권한으로는 상품을 수정할 수 없습니다.");

            default:
                throw new IllegalArgumentException("유효하지 않은 권한입니다.");
        }
    }

    private void checkDeleteRole(String role, Long userId, UUID companyHubId) throws AccessDeniedException {
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
                    throw new AccessDeniedException("해당 업체의 상품을 삭제할 권한이 없습니다.");
                }
                break;

            case "DELIVERY_MANAGER":
            case "COMPANY_MANAGER":
                // 배송 담당자와 업체 담당자는 삭제 권한 없음
                throw new AccessDeniedException("해당 권한으로는 업체를 삭제할 수 없습니다.");

            default:
                throw new IllegalArgumentException("유효하지 않은 권한입니다.");
        }
    }

    private void checkReadyRole(String role, Long userId, UUID companyHubId) throws AccessDeniedException {
        switch (role) {
            case "MASTER":
            case "DELIVERY_MANAGER":
            case "COMPANY_MANAGER":
                // MASTER 는 모든 작업 가능, 권한 검증 필요 없음
                // DELIVERY_MANAGER, COMPANY_MANAGER 는 모든 조회 및 검색 가능
                break;

            case "HUB_MANAGER":
                // HubClient 를 사용하여 companyHubId를 기반으로 허브 정보를 조회
                HubDto hub = hubClient.getHubById(companyHubId).getBody().getData();
                Long hubManagerId = hub.getHubManagerId();

                // 허브 ID에 매핑된 허브 관리자 ID와 현재 userId 비교
                if (!userId.equals(hubManagerId)) {
                    throw new AccessDeniedException("해당 업체의 상품을 조회할 권한이 없습니다.");
                }
                break;


            default:
                throw new IllegalArgumentException("유효하지 않은 권한입니다.");
        }
    }
}
