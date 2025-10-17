package com.lumenlabs.energymanagement.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.lumenlabs.energymanagement.model.Department;

public interface DepartmentRepository extends JpaRepository<Department, UUID> {
	Boolean existsByCompanyIdAndNameIgnoreCase(UUID companyId, String name);
	Page<Department> findAllByCompanyIdAndNameContainingIgnoreCase(UUID companyId, String name, Pageable pageable);
	Optional<Department> findByCompanyIdAndId(UUID companyId, UUID id);
	boolean existsByCompanyIdAndNameIgnoreCaseAndIdNot(UUID companyId, String name, UUID id);
	boolean existsByCompanyIdAndId(UUID companyId, UUID departmentId);
}
