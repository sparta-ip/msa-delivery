package com.msa_delivery.delivery.application.util;

import com.msa_delivery.delivery.domain.model.DeliveryManagerType;
import com.msa_delivery.delivery.infrastructure.client.HubClient;
import com.msa_delivery.delivery.infrastructure.client.HubDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CheckRole {

    private final HubClient hubClient;

    // 배송, 배송 경로에 대한 권한
    public void validateRole(String userId, String username, String role, UUID departureId, UUID arrivalId,
                             Long deliveryManagerId, String action) throws AccessDeniedException {
        switch (role) {
            case "MASTER":
                // MASTER 는 모든 작업 가능, 권한 검증 필요 없음
                return;

            case "HUB_MANAGER":
                if ("수정".equals(action) || "삭제".equals(action) || "조회".equals(action)) {
                    // 출발 허브와 도착 허브 권한 확인
                    if (!isHubManager(userId, username, role, departureId) &&
                            !isHubManager(userId, username, role, arrivalId)) {
                        throw new AccessDeniedException(String.format("해당 배송 정보를 %s할 권한이 없습니다.", action));
                    }
                } else {
                    throw new AccessDeniedException("허브 관리자는 배송을 생성할 수 없습니다.");
                }
                break;

            case "DELIVERY_MANAGER":
                if ("수정".equals(action) || "조회".equals(action)) {
                    if (!Long.valueOf(userId).equals(deliveryManagerId)) {
                        throw new AccessDeniedException(String.format("본인의 배송 정보만 %s할 수 있습니다.", action));
                    }
                } else {
                    throw new AccessDeniedException(String.format("배송 담당자는 배송 정보를 %s할 권한이 없습니다.", action));
                }
                break;

            case "COMPANY_MANAGER":
                if ("조회".equals(action)) {
                    break; // 업체 담당자는 조회만 가능
                }
                throw new AccessDeniedException(String.format("업체 담당자는 배송 정보를 %s할 수 없습니다.", action));

            default:
                throw new IllegalArgumentException("유효하지 않은 권한입니다.");
        }
    }

    public void validateManagerRole(String userId, String username, String role, UUID hubId, Long deliveryManagerId, String action, DeliveryManagerType type) throws AccessDeniedException {
        switch (role) {
            case "MASTER":
                // MASTER 는 모든 작업 가능, 권한 검증 필요 없음
                break;

            case "HUB_MANAGER":
                // 허브 관리자 권한 확인
                if (!isDeliveryManagerForHub(userId, username, role, hubId, type)) {
                    throw new AccessDeniedException(String.format("허브 관리자는 본인 허브에만 배송 담당자를 %s할 수 있습니다.", action));
                }
                break;

            case "DELIVERY_MANAGER":
                // 배송 담당자는 본인 정보만 조회 가능
                if (!"조회".equals(action) || !Long.valueOf(userId).equals(deliveryManagerId)) {
                    throw new AccessDeniedException("배송 담당자는 본인 정보만 조회할 수 있습니다.");
                }
                break;

            case "COMPANY_MANAGER":
                // 업체 담당자는 모든 작업 불가
                throw new AccessDeniedException(String.format("업체 담당자는 배송 담당자를 %s할 권한이 없습니다.", action));

            default:
                throw new IllegalArgumentException("유효하지 않은 권한입니다.");
        }
    }

    /**
     * 허브 관리자인지 확인
     */
    public boolean isHubManager(String userId, String username, String role, UUID hubId) {
        if (hubId == null) return false;

        HubDto hub = hubClient.getHubById(hubId, userId, username, role).getBody().getData();

        return Long.valueOf(userId).equals(hub.getHubManagerId());
    }

    /**
     * 허브 관리자가 특정 배송 담당자를 관리할 권한이 있는지 확인
     */
    private boolean isDeliveryManagerForHub(String userId, String username, String role, UUID hubId, DeliveryManagerType type) throws AccessDeniedException {
        if (type.equals(DeliveryManagerType.COMPANY_DELIVERY_MANAGER)) {
            if (hubId == null) {
                throw new IllegalArgumentException("업체 배송 담당자의 경우 허브 ID는 필수입니다.");
            }

            HubDto hub = hubClient.getHubById(hubId, userId, username, role).getBody().getData();
            if (!Long.valueOf(userId).equals(hub.getHubManagerId())) {
                throw new AccessDeniedException("본인이 담당하는 허브에만 업체 배송 담당자를 생성/수정할 수 있습니다.");
            }
        } else if (type.equals(DeliveryManagerType.HUB_DELIVERY_MANAGER) && hubId != null) {
            // 허브 배송 담당자는 허브 ID를 설정할 수 없음
            throw new IllegalArgumentException("허브 배송 담당자는 허브 ID를 설정할 수 없습니다.");
        }
        return true;
    }
}
