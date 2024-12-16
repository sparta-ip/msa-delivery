package com.msa_delivery.gateway.presentation.controller;

import com.msa_delivery.gateway.application.dtos.ApiResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RequestMapping("/api")
@RestController
public class FallbackController {

    @GetMapping("/fallback")
    public Mono<ResponseEntity<ApiResponseDto<?>>> getFallback(ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponseDto.response(HttpStatus.SERVICE_UNAVAILABLE.value(),
                        "Get : request is currently unavailable. Please try again later.",
                        "")));
    }

    @PostMapping("/fallback")
    public Mono<ResponseEntity<ApiResponseDto<?>>> postFallback(ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponseDto.response(HttpStatus.SERVICE_UNAVAILABLE.value(),
                        "Post : request is currently unavailable. Please try again later.",
                        "")));
    }

    @PutMapping("/fallback")
    public Mono<ResponseEntity<ApiResponseDto<?>>> putFallback(ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponseDto.response(HttpStatus.SERVICE_UNAVAILABLE.value(),
                        "Put : request is currently unavailable. Please try again later.",
                        "")));
    }

    @DeleteMapping("/fallback")
    public Mono<ResponseEntity<ApiResponseDto<?>>> deleteFallback(ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponseDto.response(HttpStatus.SERVICE_UNAVAILABLE.value(),
                        "Delete : request is currently unavailable. Please try again later.",
                        "")));
    }
}
