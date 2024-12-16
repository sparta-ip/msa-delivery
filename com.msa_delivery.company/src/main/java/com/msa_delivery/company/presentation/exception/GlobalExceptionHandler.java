package com.msa_delivery.company.presentation.exception;

import com.msa_delivery.company.application.dto.CommonResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CommonResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                CommonResponse.error(HttpStatus.BAD_REQUEST.value(), ex.getMessage())
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<CommonResponse<Void>> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                CommonResponse.error(HttpStatus.FORBIDDEN.value(), ex.getMessage())
        );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<CommonResponse<Void>> handleEntityNotFoundException(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                CommonResponse.error(HttpStatus.NOT_FOUND.value(), ex.getMessage())
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<Void>> handleGeneralException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                CommonResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버 내부 오류가 발생했습니다.")
        );
    }
}