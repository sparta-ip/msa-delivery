package com.msa_delivery.delivery.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponseDto<T> {
    private int status;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private T data;

    public static <T> ApiResponseDto<T> response(int status, String message, T data) {
        return ApiResponseDto.<T>builder()
                .status(status)
                .message(message)
                .data(data)
                .build();
    }
}

