package com.msa_delivery.order.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResponseDto<T> {
    private int status;
    private String message;
    private T data;
}
