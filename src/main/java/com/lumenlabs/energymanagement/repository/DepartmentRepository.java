package com.lumenlabs.energymanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lumenlabs.energymanagement.model.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
	List<Department> findAllByCompanyId(Long companyId);
}
