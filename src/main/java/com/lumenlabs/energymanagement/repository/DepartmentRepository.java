package com.lumenlabs.energymanagement.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.lumenlabs.energymanagement.model.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
	Boolean existsByCompanyIdAndName(Long companyId, String name);
	Page<Department> findAllByCompanyIdAndNameContaining(Long companyId, String name, Pageable pageable);
	Optional<Department> findByCompanyIdAndId(Long companyId, Long id);
	boolean existsByCompanyIdAndNameAndIdNot(Long companyId, String name, Long id);
}
