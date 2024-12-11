package com.msa_delivery.user.application.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

