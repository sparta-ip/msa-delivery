package com.msa_delivery.auth.presentation.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RestApiException {

    private int status;

    @JsonProperty("error_message")
    private String error;
}
