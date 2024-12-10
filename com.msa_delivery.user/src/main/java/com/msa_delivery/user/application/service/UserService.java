package com.msa_delivery.user.application.service;

import com.msa_delivery.user.application.UserSearchDto;
import com.msa_delivery.user.application.dtos.UserDetailResponseDto;
import com.msa_delivery.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // TODO : search는 querydsl을 사용하여 동적으로 구현 필요.(뤼튼에 물어봤음)
    public Page<UserDetailResponseDto> searchOrders(UserSearchDto userSearchDto, Pageable pageable, String userId, String role) {

        return null;
    }
}
