package com.msa_delivery.order.application.exception;

import com.msa_delivery.order.application.dto.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ResponseDto<?>> handleProductNotFound(ProductNotFoundException ex) {
        ResponseDto<?> response = new ResponseDto<>(HttpStatus.NOT_FOUND.value(), ex.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(OrderCreationException.class)
    public ResponseEntity<ResponseDto<?>> handleOrderCreationException(OrderCreationException ex) {
        ResponseDto<?> response = new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<?>> handleException(Exception ex) {
        ResponseDto<?> response = new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버 내부 오류", null);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
