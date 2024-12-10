package com.msa_delivery.auth.controller;

import com.msa_delivery.auth.application.dtos.ApiResponseDto;
import com.msa_delivery.auth.application.dtos.AuthRequestDto;
import com.msa_delivery.auth.application.dtos.AuthResponseDto;
import com.msa_delivery.auth.application.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/sign-in")
    public ResponseEntity<ApiResponseDto<?>> signIn(@RequestBody AuthRequestDto authRequestDto
    ) {
        return ResponseEntity.status(HttpStatus.OK.value())
                .header("Authorization", authService.signIn(authRequestDto))
                .body(ApiResponseDto.response(200,
                        "로그인에 성공하였습니다.",
                        ""));
    }

    @PostMapping("/sign-up")
    public ResponseEntity<ApiResponseDto<? extends AuthResponseDto>> signUp(@RequestBody AuthRequestDto authRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.response(HttpStatus.CREATED.value(),
                        "회원가입에 성공하였습니다.",
                        authService.signUp(authRequestDto)));
    }

    // TODO : schema 확인하기. 없다고 뜸
}
