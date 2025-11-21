package com.lumenlabs.energymanagement.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lumenlabs.energymanagement.enums.Role;
import com.lumenlabs.energymanagement.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
	Optional<User> findByEmailIgnoreCase(String email);
	Optional<User> findByCompanyId(UUID id);
	boolean existsByEmailIgnoreCaseAndIdNot(String email, UUID id);
	Page<User> findAllByCompanyIdAndNameContainingIgnoreCase(UUID companyId, String name, Pageable pageable);
	Optional<User> findByCompanyIdAndId(UUID companyId, UUID id);
	boolean existsByEmailIgnoreCase(String email);
	int countByRoleAndCompanyId(Role admin, UUID companyId);
}
