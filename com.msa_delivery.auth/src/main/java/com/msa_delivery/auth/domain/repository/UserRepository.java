package com.msa_delivery.auth.domain.repository;

import com.msa_delivery.auth.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
