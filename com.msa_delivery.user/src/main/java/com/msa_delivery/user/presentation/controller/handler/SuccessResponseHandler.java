package com.msa_delivery.user.presentation.controller.handler;

import com.msa_delivery.user.application.dtos.ApiPageResponseDto;
import com.msa_delivery.user.application.dtos.ApiResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class SuccessResponseHandler {

    public <T> ResponseEntity<ApiResponseDto<T>> handleSuccess(HttpStatus status, String message, T dto) {
        ApiResponseDto<T> response = new ApiResponseDto<>(
                status.value(),
                message,
                dto
        );
        return ResponseEntity.status(status).body(response);
    }

    public <T> ResponseEntity<ApiPageResponseDto<T>> handlePageSuccess(HttpStatus status, String message, Page<T> page) {
        List<T> content = page.getContent();

        ApiPageResponseDto<T> response = new ApiPageResponseDto<>(
                status.value(),
                message,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                content
        );
        return ResponseEntity.status(status).body(response);
    }
}
