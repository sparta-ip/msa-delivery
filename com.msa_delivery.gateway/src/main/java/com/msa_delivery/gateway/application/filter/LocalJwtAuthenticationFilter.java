package com.msa_delivery.gateway.application.filter;

import com.msa_delivery.gateway.application.service.AuthService;
import com.msa_delivery.gateway.infrastructure.dtos.VerifyUserDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class LocalJwtAuthenticationFilter implements GlobalFilter {

    private final AuthService authService;

    @Value("${service.jwt.secret-key}")
    private String secretKey;

    private static final String KEY_ALGORITHM = "HmacSHA256";

    public LocalJwtAuthenticationFilter(@Lazy AuthService authService) {
        this.authService = authService;
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(keyBytes, KEY_ALGORITHM);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (path.startsWith("/api/auth") || path.equals("/api/fallback")) {
            return chain.filter(exchange);
        }

        String token = extractToken(exchange);

        exchange = validateToken(token, exchange);

        if (token == null || exchange.getResponse().getStatusCode() == HttpStatus.UNAUTHORIZED) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }

    private String extractToken(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    private ServerWebExchange validateToken(String token, ServerWebExchange exchange) {
        try {
            SecretKey key = getSigningKey();
            Jws<Claims> claimsJws = Jwts.parser()
                    .verifyWith(key)
                    .build().parseSignedClaims(token);
            Claims claims = claimsJws.getPayload();

            Date expiration = claims.getExpiration();
            if (expiration.before(new Date())) {
                return exchange;
            }

            if ((claims.get("userId") != null) || (claims.get("username") != null) || (claims.get("role") != null)) {
                String userId = claims.get("userId").toString();
                String username = claims.get("username").toString();
                String role = claims.get("role").toString();

                Boolean verifiedUser = authService.verifyUser(VerifyUserDto.builder()
                        .userId(userId)
                        .username(username)
                        .role(role)
                        .build());

                if (verifiedUser) {
                    ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                            .header("X-User_Id", userId)
                            .header("X-Username", username)
                            .header("X-Role", role)
                            .build();

                    // 교환의 요청을 업데이트
                    exchange = exchange.mutate().request(mutatedRequest).build();
                    return exchange;
                }
            }
            return exchange;
        } catch (Exception e) {
            log.info(e.getMessage());
            return exchange;
        }
    }
}
