package com.lumenlabs.energymanagement.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lumenlabs.energymanagement.model.User;

public interface UserRepository extends JpaRepository<User, UUID> {
	Optional<User> findByEmail(String email);
	Optional<User> findByCompanyId(UUID id);
	boolean existsByEmailAndIdNot(String email, UUID id);
}
