package com.msa_delivery.order.domain.repository;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        if (requestAttributes == null) {
            log.warn("RequestAttributes is null - AuditorAwareImpl cannot retrieve current user.");
            return Optional.empty();
        }

        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        String username = request.getHeader("X-Username");

        log.info("Request Header X-Username: {}", username);

        if (username == null) {
            log.warn("X-Username header is missing.");
            return Optional.empty();
        }

        return Optional.of(username);
    }
}
