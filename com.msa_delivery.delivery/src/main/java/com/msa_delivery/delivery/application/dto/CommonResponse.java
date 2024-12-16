package com.msa_delivery.delivery.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL) // null 필드 제외
public class CommonResponse<T> {

    private final int status;
    private final String message;
    private final T data;

    private CommonResponse(int status, String message, T data) {
        this.status = status; // HttpStatus 의 값(숫자) 가져오기
        this.message = message; // 메시지 없으면 기본 메시지 사용
        this.data = data;
    }

    // 성공 응답 생성 메서드
    public static <T> CommonResponse<T> success(int status, String message, T data) {
        return new CommonResponse<>(status, message, data);
    }

    // 실패 응답 생성 메서드
    public static CommonResponse<Void> error(int status, String message) {
        return new CommonResponse<>(status, message, null);
    }
}