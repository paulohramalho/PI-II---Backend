package com.lumenlabs.energymanagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lumenlabs.energymanagement.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);
	Optional<User> findByCompanyId(Long id);
	boolean existsByEmailAndIdNot(String email, Long id);
}
