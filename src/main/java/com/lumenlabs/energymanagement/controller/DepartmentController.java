package com.lumenlabs.energymanagement.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.lumenlabs.energymanagement.dto.department.DepartmentDTO;
import com.lumenlabs.energymanagement.dto.department.DepartmentRegistrationDTO;
import com.lumenlabs.energymanagement.model.Department;
import com.lumenlabs.energymanagement.service.DepartmentService;
import com.lumenlabs.energymanagement.util.SecurityUtils;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/department")
public class DepartmentController {
	
	@Autowired
	private DepartmentService departmentService;
	
	@Autowired
	private SecurityUtils securityUtils;
	
	@GetMapping("/{id}")
	public ResponseEntity<DepartmentDTO> getDepartment(@PathVariable Long id){
		return ResponseEntity.ok(departmentService.getDepartment(securityUtils.getLoggedUserCompany().getId(), id));
	}
	
	@GetMapping
	public ResponseEntity<Page<DepartmentDTO>> getAll(@RequestParam(required = false, defaultValue = "") String name, Pageable pageable){
		return ResponseEntity.ok(departmentService.getAll(securityUtils.getLoggedUserCompany().getId(), name, pageable));
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping
	public ResponseEntity<?> createDepartment(@RequestBody @Valid DepartmentRegistrationDTO departmentRegistrationDTO){
		Department department = departmentService.createDepartment(securityUtils.getLoggedUserCompany(), departmentRegistrationDTO);
		
		URI location = ServletUriComponentsBuilder
	            .fromCurrentRequest()
	            .path("/{id}")
	            .buildAndExpand(department.getId())
	            .toUri();

	    return ResponseEntity.created(location).build();
	}

}
