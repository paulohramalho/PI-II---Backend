package com.lumenlabs.energymanagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.lumenlabs.energymanagement.model.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
	Page<Department> findAllByCompanyIdAndNameContaining(Long companyId, String name, Pageable pageable);
}
