package com.msa_delivery.hub.presentation.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class ApiResponse<T> {

    private int status;
    private String message;
    private Object data;


}
