package com.msa_delivery.order.application.exception;

public class GeminiServiceException extends RuntimeException {
    public GeminiServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
