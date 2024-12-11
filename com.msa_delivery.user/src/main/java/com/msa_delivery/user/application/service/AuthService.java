package com.msa_delivery.user.application.service;


import com.msa_delivery.user.infrastructure.dtos.VerifyUserDto;

public interface AuthService {
    Boolean verifyUser(VerifyUserDto verifyUserDto);
}
