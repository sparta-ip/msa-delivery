package com.msa_delivery.user.application.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ApiPageResponseDto<T> extends ApiResponseDto<T>{
    private int size;
    private int number;
    private int totalElements;
    private int totalPages;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private T data;
}
