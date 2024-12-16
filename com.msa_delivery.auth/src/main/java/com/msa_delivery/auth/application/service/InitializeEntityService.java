package com.msa_delivery.auth.application.service;

import com.msa_delivery.auth.domain.entity.User;
import com.msa_delivery.auth.domain.entity.UserRoleEnum;
import com.msa_delivery.auth.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InitializeEntityService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Boolean initializeEntity() {
        try {
            if (userRepository.existsByUsername("master0")) {
                throw new IllegalArgumentException("initialize can not done twice.");
            }

            List<User> users = new ArrayList<>();

            users.add(createUser("master0", "aA123123!", UserRoleEnum.MASTER, "masterSlack0"));

            for (int i = 0; i < 2; i++) {
                users.add(createUser("company" + Integer.toString(i), "aA123123!", UserRoleEnum.COMPANY_MANAGER, "companySlack" + Integer.toString(i)));
            }

            for (int i = 0; i < 2; i++) {
                users.add(createUser("delivery" + Integer.toString(i), "aA123123!", UserRoleEnum.DELIVERY_MANAGER, "deliverySlack" + Integer.toString(i)));
            }

            for (int i = 0; i < 17; i++) {
                users.add(createUser("hubs" + Integer.toString(i), "aA123123!", UserRoleEnum.HUB_MANAGER, "slack" + Integer.toString(i)));
            }

            userRepository.saveAll(users);

            return true;
        } catch (Exception e) {
            throw new IllegalArgumentException("initialize can not done twice.");
        }
    }

    private User createUser(String username, String password, UserRoleEnum role, String slackId) {
        String encodedPassword = passwordEncoder.encode(password);

        return User.builder()
                .username(username)
                .password(encodedPassword)
                .role(role)
                .slackId(slackId)
                .createdBy("SYSTEM")
                .updatedBy("SYSTEM")
                .build();
    }
}
