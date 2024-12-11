package com.msa_delivery.user.domain.repository;

import com.msa_delivery.user.domain.entity.User;
import com.msa_delivery.user.infrastructure.repository.CustomUserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, CustomUserRepository {
    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);
}
