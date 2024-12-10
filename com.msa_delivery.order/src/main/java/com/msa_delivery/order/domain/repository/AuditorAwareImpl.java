package com.msa_delivery.order.domain.repository;

import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        if (requestAttributes == null) {
            return Optional.empty();
        }

        String username = (String) requestAttributes.getAttribute("X-Username",
            RequestAttributes.SCOPE_REQUEST);

        if (username == null) {
            return Optional.empty();
        }

        return Optional.of(username);
    }
}
