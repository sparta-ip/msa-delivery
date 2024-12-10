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

    // 주문 수정에 대해 AOP 역할 검사 적용
    @Pointcut("execution(* com.msa_delivery.order.presentation.controller.OrderController.updateOrder(..))")
    public void orderUpdatePointcut() {
    }

    // 주문 수정 전 사용자 권한 검사
    @Before("orderUpdatePointcut() && args(orderRequestDto, user_id, username, role, order_id)")
    public void checkUserRole(Object orderRequestDto, Long user_id, String username, String role, UUID order_id) {
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

        // 3. 위 조건에 맞지 않는 경우, 접근 불가 예외 발생
        log.warn("Access denied for user_id: {}, role: {}, order_id: {}", user_id, role, order_id);
        throw new AccessDeniedException("접근 권한이 없습니다.");
    }
}
