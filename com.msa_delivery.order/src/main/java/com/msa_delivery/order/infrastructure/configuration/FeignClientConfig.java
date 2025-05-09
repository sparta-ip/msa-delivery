package com.msa_delivery.order.infrastructure.configuration;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignClientConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            HttpServletRequest request = getCurrentHttpRequest();
            if (request != null) {
                String user_id = request.getHeader("X-User_Id");
                String username = request.getHeader("X-Username");
                String role = request.getHeader("X-Role");

                if (user_id != null) requestTemplate.header("X-User_Id", user_id);
                if (username != null) requestTemplate.header("X-Username", username);
                if (role != null) requestTemplate.header("X-Role", role);
            }
        };
    }

    private HttpServletRequest getCurrentHttpRequest() {
        try {
            return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        } catch (Exception e) {
            return null;
        }
    }
}
