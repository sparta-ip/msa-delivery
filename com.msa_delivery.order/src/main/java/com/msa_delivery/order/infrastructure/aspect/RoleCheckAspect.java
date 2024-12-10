package com.msa_delivery.order.infrastructure.aspect;

import com.msa_delivery.order.application.exception.AccessDeniedException;
import com.msa_delivery.order.application.service.HubService;
import com.msa_delivery.order.application.service.OrderService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RoleCheckAspect {

    private final OrderService orderService;
    private final HubService hubService;

    // 주문 수정 권한 검사
    private void checkUserRoleForUpdate(Long user_id, String username, String role, UUID order_id) {
        // DELIVERY_MANAGER 또는 COMPANY_MANAGER 권한이 있는 경우 수정 불가
        if ("DELIVERY_MANAGER".equals(role) || "COMPANY_MANAGER".equals(role)) {
            log.warn("DELIVERY_MANAGER, COMPANY_MANAGER role cannot update orders. Access denied for user_id: {}, role: {}", user_id, role);
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }

        checkUserRoleForOrder(user_id, username, role, order_id);
    }

    // 주문 삭제 권한 검사
    private void checkUserRoleForDelete(Long user_id, String username, String role, UUID order_id) {
        // DELIVERY_MANAGER 또는 COMPANY_MANAGER 권한이 있는 경우 삭제 불가
        if ("DELIVERY_MANAGER".equals(role) || "COMPANY_MANAGER".equals(role)) {
            log.warn("DELIVERY_PERSONNEL, COMPANY_MANAGER role cannot delete orders. Access denied for user_id: {}, role: {}", user_id, role);
            throw new AccessDeniedException("삭제 권한이 없습니다.");
        }

        checkUserRoleForOrder(user_id, username, role, order_id);
    }

    // 권한 검사
    private void checkUserRoleForOrder(Long user_id, String username, String role, UUID order_id) {
        log.info("AOP Role Check - user_id: {}, username: {}, role: {}, orderId: {}", user_id, username, role, order_id);

        // 1. 마스터 관리자 권한이 있는 경우 통과
        if ("MASTER".equals(role)) {
            log.info("MASTER_ADMIN role detected. Access granted.");
            return;
        }

        // 2. 허브 관리자인 경우, 해당 주문의 허브와 연결된 사용자인지 검증
        if ("HUB_MANAGER".equals(role)) {
            // 주문과 연결된 허브 ID 리스트 조회
            List<UUID> hubIdsOfOrder = orderService.getHubIdByOrderId(order_id);
            log.info("Order ID: {} is linked to the following Hub IDs: {}", order_id, hubIdsOfOrder);

            // 허브 관리자 확인
            boolean hasAccess = hubIdsOfOrder.stream().anyMatch(hub_id -> {
                try {
                    Long hubManagerId = hubService.getHub(hub_id).getHub_manager_id();
                    log.info("Hub ID: {}, Hub Manager ID: {}, Current User ID: {}", hub_id, hubManagerId, user_id);
                    return hubManagerId != null && hubManagerId.equals(user_id);
                } catch (Exception e) {
                    log.error("Error occurred while retrieving Hub ID: {} - {}", hub_id, e.getMessage());
                    return false;
                }
            });

            if (hasAccess) {
                log.info("Access granted to HUB_MANAGER for user_id: {}", user_id);
                return; // 허브와 연결된 사용자인 경우 접근 허용
            }
        }

        // 3. 업체 담당자는 본인 주문만 조회 가능
        if ("COMPANY_MANAGER".equals(role)) {

            // 주문과 연결된 업체 담당자 ID 리스트 조회
            List<Long> companyMangersId = orderService.getCompanyManagersIdByOrderId(order_id);
            log.info("Order ID: {} is linked to the following companyMangersId: {}", order_id, companyMangersId);

            // 업체 담당자 ID 와 일치하는 user_id 가 있는지 확인
            if (!companyMangersId.contains(user_id)) {
                log.warn("Access denied for user_id: {}, role: {}, order_id: {}. User is not authorized to access this order.", user_id, role, order_id);
                throw new AccessDeniedException("본인 주문에 대해서만 확인할 수 있습니다.");
            }
        }

        // 4. 배송 담당자는 본인 주문만 조회 가능
        if ("DELIVERY_MANAGER".equals(role)) {
            // 주문과 연결된 배송 담당자 ID 리스트 조회
            List<Long> deliveryManagerIds = orderService.getDeliveryManagerIdsByOrderId(order_id);
            log.info("Order ID: {} is linked to the following deliveryManagerIds: {}", order_id, deliveryManagerIds);

            // 배송 담당자 ID와 일치하는 user_id가 있는지 확인
            if (!deliveryManagerIds.contains(user_id)) {
                log.warn("Access denied for user_id: {}, role: {}, order_id: {}. User is not authorized to access this order.", user_id, role, order_id);
                throw new AccessDeniedException("본인 주문에 대해서만 확인할 수 있습니다.");
            }
        }

        // 4. 위 조건에 맞지 않는 경우, 접근 불가 예외 발생
        log.warn("Access denied for user_id: {}, role: {}, order_id: {}", user_id, role, order_id);
        throw new AccessDeniedException("접근 권한이 없습니다.");
    }

    // 주문 수정에 대해 AOP 역할 검사 적용
    @Pointcut("execution(* com.msa_delivery.order.presentation.controller.OrderController.updateOrder(..))")
    public void orderUpdatePointcut() {
    }

    // 주문 삭제에 대해 AOP 역할 검사 적용
    @Pointcut("execution(* com.msa_delivery.order.presentation.controller.OrderController.deleteOrder(..))")
    public void orderDeletePointcut() {
    }

    // 주문 조회에 대해 AOP 역할 검사 적용
    @Pointcut("execution(* com.msa_delivery.order.presentation.controller.OrderController.getOrder(..))")
    public void orderGetPointcut() {
    }

    // 주문 수정 전 사용자 권한 검사
    @Before("orderUpdatePointcut() && args(orderRequestDto, user_id, username, role, order_id)")
    public void checkUserRoleForUpdate(Object orderRequestDto, Long user_id, String username, String role, UUID order_id) {
        checkUserRoleForUpdate(user_id, username, role, order_id);
    }

    // 주문 삭제 전 사용자 권한 검사
    @Before("orderDeletePointcut() && args(orderRequestDto, user_id, username, role, order_id)")
    public void checkUserRoleForDelete(Object orderRequestDto, Long user_id, String username, String role, UUID order_id) {
        checkUserRoleForDelete(user_id, username, role, order_id);
    }

    // 주문 조회 전 사용자 권한 검사
    @Before("orderGetPointcut() && args(user_id, username, role, order_id)")
    public void checkUserRoleForGet(Long user_id, String username, String role, UUID order_id) {
        checkUserRoleForOrder(user_id, username, role, order_id);
    }
}
