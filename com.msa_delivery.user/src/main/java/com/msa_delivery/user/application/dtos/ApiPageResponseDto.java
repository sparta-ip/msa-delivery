package com.msa_delivery.user.application.dtos;

import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
public class ApiPageResponseDto<T> extends ApiResponseDto<List<T>>{
    private int size;
    private int number;
    private long totalElements;
    private int totalPages;

    public ApiPageResponseDto(int status, String message, int number, int size, long totalElements, int totalPages, List<T> data) {
        super(status, message, data);
        this.number = number + 1;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }
}
