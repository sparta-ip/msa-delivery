package com.msa_delivery.auth.presentation.controller;

import com.msa_delivery.auth.application.dtos.ApiResponseDto;
import com.msa_delivery.auth.application.dtos.AuthRequestDto;
import com.msa_delivery.auth.application.dtos.AuthResponseDto;
import com.msa_delivery.auth.application.service.AuthService;
import com.msa_delivery.auth.application.service.InitializeEntityService;
import com.msa_delivery.auth.infrastructure.dtos.VerifyUserDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@Validated
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final InitializeEntityService initializeEntityService;

    @PostMapping("/sign-in")
    public ResponseEntity<ApiResponseDto<?>> signIn(@RequestBody AuthRequestDto authRequestDto
    ) {
        return authService.signIn(authRequestDto);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<ApiResponseDto<?>> signUp(@Valid @RequestBody AuthRequestDto authRequestDto) {
        return authService.signUp(authRequestDto);
    }

    @PostMapping("/verify")
    public Boolean verifyUser(@RequestBody VerifyUserDto verifyUserDto) {
        return authService.verifyUser(verifyUserDto);
    }

    @GetMapping("/initialize")
    public Boolean initializeEntity() {
        return initializeEntityService.initializeEntity();
    }
}
