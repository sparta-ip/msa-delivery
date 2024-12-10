package com.msa_delivery.company.application.service;

import com.msa_delivery.company.application.dto.CompanyDto;
import com.msa_delivery.company.application.mapper.HubMapper;
import com.msa_delivery.company.domain.model.Company;
import com.msa_delivery.company.domain.model.CompanyType;
import com.msa_delivery.company.domain.repository.CompanyRepository;
import com.msa_delivery.company.infrastructure.client.HubClient;
import com.msa_delivery.company.infrastructure.client.HubDto;
import com.msa_delivery.company.infrastructure.client.UserClient;
import com.msa_delivery.company.infrastructure.client.UserDto;
import com.msa_delivery.company.presentation.request.CompanyRequest;
import com.msa_delivery.company.presentation.request.CompanyUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserClient userClient;
    private final HubClient hubClient;

    @Transactional
    public CompanyDto createCompany(CompanyRequest request, Long userId, String username, String role) throws AccessDeniedException {

        String address = request.getAddress();  // 업체 주소

        // HubClient 연동 & HubMapper 이용
        // HubMapper 를 통해 address 기반으로 hubName 추출
        String hubName = HubMapper.getHubByAddress(HubMapper.getHubByAddress(address));
        HubDto hub = hubClient.getHubs(hubName).getBody().getData();
        UUID hubId= hub.getHubId(); // 업체 관리 허브 ID
        Long hubManagerId = hub.getHubManagerId();  // 허브 담당자 ID;

        // 권한 체크
        checkCreateRole(role, userId, hubManagerId);

        // UserClient 연동
        // managerId 체크 후 userId, slackId 반환
        UserDto user = userClient.getUserById(request.getManagerId()).getBody().getData();
        Long managerId = user.getUserId();  // 업체 담당자 ID
        String slackId = user.getSlackId(); // 업체 담당자 Slack ID

        String name = request.getName();    // 업체명
        CompanyType type = CompanyType.fromString(request.getType());   // 업체 타입

        // create
        Company company = Company.create(managerId, slackId, hubId, name, address, type);
        company.setCreatedBy(username);

        // save
        Company saveCompany = companyRepository.save(company);

        // Dto 생성
        return CompanyDto.create(saveCompany);
    }

    @Transactional
    public CompanyDto updateCompany(UUID companyId, CompanyUpdateRequest request, Long userId, String username, String role) throws AccessDeniedException {
        // 기존 데이터 조회
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 업체를 찾을 수 없습니다."));
        Long companyManagerId = company.getManagerId(); // 기존 업체 담당자 ID
        UUID companyHubId = company.getHubId();         // 기존 업체 허브 ID

        // 권한 체크
        checkUpdateRole(role, userId, companyHubId, companyManagerId);

        // 주소, 허브 ID 업데이트
        String address = request.getAddress() != null ? request.getAddress() : company.getAddress();
        UUID hubId = companyHubId;
        if (request.getAddress() != null) {
            String hubName = HubMapper.getHubByAddress(address);
            HubDto hub = hubClient.getHubs(hubName).getBody().getData();
            hubId = hub.getHubId();
        }

        // 담당자 업데이트
        Long managerId = companyManagerId;
        String slackId = company.getSlackId();
        if (request.getManagerId() != null) {
            UserDto user = userClient.getUserById(request.getManagerId()).getBody().getData();
            managerId = user.getUserId();
            slackId = user.getSlackId();
        }

        String name = request.getName() != null ? request.getName() : company.getName();
        CompanyType type = request.getType() != null ? CompanyType.fromString(request.getType()) : company.getType();

        // update
        company.update(managerId, slackId, hubId, name, address, type);
        company.setUpdatedBy(username);

        return CompanyDto.create(company);
    }

    private void checkCreateRole(String role, Long userId, Long hubManagerId) throws AccessDeniedException {
        switch (role) {
            case "MASTER":
                // MASTER 는 모든 작업 가능, 권한 검증 필요 없음
                break;

            case "HUB_MANAGER":
                // 요청 헤더의 허브 ID와 매핑된 허브 ID 비교
                if (!userId.equals(hubManagerId)) {
                    throw new AccessDeniedException("해당 허브에서 업체를 생성할 권한이 없습니다.");
                }
                break;

            case "DELIVERY_MANAGER":
            case "COMPANY_MANAGER":
                // 배송 담당자와 업체 담당자는 생성 권한 없음
                throw new AccessDeniedException("해당 권한으로는 업체를 생성할 수 없습니다.");

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
                    throw new AccessDeniedException("해당 허브의 업체를 수정할 권한이 없습니다.");
                }
                break;

            case "COMPANY_MANAGER":
                // 요청 헤더의 userId와 매핑된 companyManagerId 비교
                if (!userId.equals(companyManagerId)) {
                    throw new AccessDeniedException("본인의 업체만 수정할 수 있습니다");
                }
            case "DELIVERY_MANAGER":
                // 배송 담당자와 업체 담당자는 생성 권한 없음
                throw new AccessDeniedException("해당 권한으로는 업체를 수정할 수 없습니다.");

            default:
                throw new IllegalArgumentException("유효하지 않은 권한입니다.");
        }
    }
}
