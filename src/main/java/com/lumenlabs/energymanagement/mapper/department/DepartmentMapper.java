package com.lumenlabs.energymanagement.mapper.department;

import org.springframework.stereotype.Component;

import com.lumenlabs.energymanagement.dto.department.DepartmentDTO;
import com.lumenlabs.energymanagement.dto.department.DepartmentRegistrationDTO;
import com.lumenlabs.energymanagement.model.Company;
import com.lumenlabs.energymanagement.model.Department;

@Component
public class DepartmentMapper {

	public DepartmentDTO mapToDepartmentDTO(Department department) {
		DepartmentDTO dto = new DepartmentDTO();
		dto.setId(department.getId());
		dto.setName(department.getName());
		dto.setDescription(department.getDescription());
		return dto;
	}
	
	public Department mapToDepartment(DepartmentRegistrationDTO dto, Company company) {
		Department department = new Department();
		department.setName(dto.getName());
		department.setDescription(dto.getDescription());
		department.setCompany(company);
		return department;
	}
	
}
