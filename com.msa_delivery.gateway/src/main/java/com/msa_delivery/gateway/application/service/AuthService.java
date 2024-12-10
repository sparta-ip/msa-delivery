package com.msa_delivery.gateway.application.service;

import com.msa_delivery.gateway.infrastructure.dtos.VerifyUserDto;

public interface AuthService {
    Boolean verifyUser(VerifyUserDto verifyUserDto);
}
