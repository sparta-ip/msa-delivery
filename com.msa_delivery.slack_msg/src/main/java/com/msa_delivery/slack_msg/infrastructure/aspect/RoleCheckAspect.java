package com.msa_delivery.slack_msg.infrastructure.aspect;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RoleCheckAspect {

    // 모든 컨트롤러 메서드에 대해 포인트컷 정의
    @Pointcut("execution(* com.msa_delivery.slack_msg.presentation.controller.SlackMsgController.*(..))")
    public void controllerMethods() {}

    // 권한 검증
    @Before("controllerMethods() && args(.., role)")
    public void checkRole(JoinPoint joinPoint, String role) {
        String methodName = joinPoint.getSignature().getName();

        log.info("Checking role for method: {} with role: {}", methodName, role);

        // MASTER는 모든 메서드에 접근할 수 있음
        if (isMasterRole(role)) {
            log.info("MASTER role has access to method: {}", methodName);
            return;
        }

        // HUB_MANAGER, DELIVERY_MANAGER, COMPANY_MANAGER 역할은 생성만 가능
        if (isLimitedRole(role)) {
            // "생성"만 허용하는 로직 수정
            if (!methodName.equals("createSlackMsg")) {
                log.warn("Access denied for role: {} to method: {}", role, methodName);
                throw new SecurityException("접근 권한이 없습니다.");
            }
        } else {
            log.warn("Unknown role: {}, access denied to method: {}", role, methodName);
            throw new SecurityException("접근 권한이 없습니다.");
        }
    }

    private boolean isMasterRole(String role) {
        return "MASTER".equalsIgnoreCase(role);
    }

    private boolean isLimitedRole(String role) {
        return List.of("HUB_MANAGER", "DELIVERY_MANAGER", "COMPANY_MANAGER").contains(role.toUpperCase());
    }
}
