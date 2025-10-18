package com.lumenlabs.energymanagement.service;

import java.util.UUID;

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
import com.lumenlabs.energymanagement.repository.DeviceRepository;

@Service
public class DepartmentService {

	@Autowired
	private DepartmentRepository departmentRepository;
	
	@Autowired
	private DeviceRepository deviceRepository;

	@Autowired
	private DepartmentMapper departmentMapper;

	public Page<DepartmentDTO> getAll(UUID companyId, String name, Pageable pageable){
		return departmentRepository.findAllByCompanyIdAndNameContainingIgnoreCase(companyId, name, pageable)
				.map(departmentMapper::mapToDepartmentDTO);
	}

	public Department createDepartment(Company company, DepartmentRegistrationDTO departmentRegistrationDTO) {
		if(departmentRepository.existsByCompanyIdAndNameIgnoreCase(company.getId(), departmentRegistrationDTO.getName()))
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Setor já existente");
		return departmentRepository.save(departmentMapper.mapToDepartment(departmentRegistrationDTO, company));
	}

	public DepartmentDTO getDepartment(UUID companyId, UUID id) {
		Department department = departmentRepository.findByCompanyIdAndId(companyId, id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Setor não encontrado"));
		return departmentMapper.mapToDepartmentDTO(department);
	}

	public void updateDepartment(Company company, DepartmentRegistrationDTO departmentRegistrationDTO, UUID id) {
		Department department = departmentRepository.findByCompanyIdAndId(company.getId(), id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Setor não encontrado"));
		if(departmentRepository.existsByCompanyIdAndNameIgnoreCaseAndIdNot(company.getId(), departmentRegistrationDTO.getName(), id))
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Setor já existente");
		departmentMapper.copyToDepartment(departmentRegistrationDTO, department);
		departmentRepository.save(department);
	}

	public void deleteDepartment(UUID companyId, UUID id) {
		Department department = departmentRepository.findByCompanyIdAndId(companyId, id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Setor não encontrado"));
		departmentRepository.delete(department);
	}
	
	public Page<DepartmentDTO> getDepartmentsByDevice(UUID companyId, UUID deviceId, String name, Pageable pageable) {
	    if (!deviceRepository.existsByCompanyIdAndId(companyId, deviceId))
	        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Dispositivo não encontrado");
	    return departmentRepository.findDepartmentsByDevice(
	            deviceId, companyId, name, pageable
	    ).map(departmentMapper::mapToDepartmentDTO);
	}

}
