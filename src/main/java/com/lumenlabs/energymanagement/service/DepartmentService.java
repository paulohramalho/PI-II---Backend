package com.lumenlabs.energymanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.lumenlabs.energymanagement.dto.department.DepartmentDTO;
import com.lumenlabs.energymanagement.dto.department.DepartmentRegistrationDTO;
import com.lumenlabs.energymanagement.mapper.department.DepartmentMapper;
import com.lumenlabs.energymanagement.model.Company;
import com.lumenlabs.energymanagement.model.Department;
import com.lumenlabs.energymanagement.repository.DepartmentRepository;

@Service
public class DepartmentService {

	@Autowired
	private DepartmentRepository departmentRepository;

	@Autowired
	private DepartmentMapper departmentMapper;

	public Page<DepartmentDTO> getAll(Long companyId, String name, Pageable pageable){
		return departmentRepository.findAllByCompanyIdAndNameContaining(companyId, name, pageable)
				.map(departmentMapper::mapToDepartmentDTO);
	}

	public Department createDepartment(Company company, DepartmentRegistrationDTO departmentRegistrationDTO) {
		if(departmentRepository.existsByCompanyIdAndName(company.getId(), departmentRegistrationDTO.getName()))
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Setor já existente");
		return departmentRepository.save(departmentMapper.mapToDepartment(departmentRegistrationDTO, company));
	}

	public DepartmentDTO getDepartment(Long companyId, Long id) {
		Department department = departmentRepository.findByCompanyIdAndId(companyId, id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Setor não encontrado"));
		return departmentMapper.mapToDepartmentDTO(department);
	}

}
