package com.lumenlabs.energymanagement.controller;

import java.net.URI;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.lumenlabs.energymanagement.dto.department.DepartmentDTO;
import com.lumenlabs.energymanagement.dto.department.DepartmentRegistrationDTO;
import com.lumenlabs.energymanagement.dto.room.RoomDTO;
import com.lumenlabs.energymanagement.model.Department;
import com.lumenlabs.energymanagement.service.DepartmentService;
import com.lumenlabs.energymanagement.service.RoomService;
import com.lumenlabs.energymanagement.util.SecurityUtils;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/department")
@Tag(name = "Setor")
public class DepartmentController {
	
	@Autowired
	private DepartmentService departmentService;
	
	@Autowired
	private RoomService roomService;
	
	@Autowired
	private SecurityUtils securityUtils;
	
	@GetMapping("/{id}")
	public ResponseEntity<DepartmentDTO> getDepartment(@PathVariable @Parameter(description = "ID do setor")  Long id){
		return ResponseEntity.ok(departmentService.getDepartment(securityUtils.getLoggedUserCompany().getId(), id));
	}
	
	@GetMapping("/{id}/room")
	public ResponseEntity<Page<RoomDTO>> getRooms(@PathVariable @Parameter(description = "ID do setor")  Long id, @RequestParam(required = false, defaultValue = "") @Parameter(description = "Nome da Sala") String name, @ParameterObject Pageable pageable){
		return ResponseEntity.ok(roomService.getRoomByDepartment(securityUtils.getLoggedUserCompany().getId(), id, name, pageable));
	}
	
	@GetMapping
	public ResponseEntity<Page<DepartmentDTO>> getAll(@RequestParam(required = false, defaultValue = "") @Parameter(description = "Nome do Setor") String name, @ParameterObject Pageable pageable){
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
	
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/{id}")
	public ResponseEntity<?> updateDepartment(@RequestBody @Valid DepartmentRegistrationDTO departmentRegistrationDTO, @PathVariable @Parameter(description = "ID do setor") Long id){
		departmentService.updateDepartment(securityUtils.getLoggedUserCompany(), departmentRegistrationDTO, id);
		return ResponseEntity.ok().build();
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteDepartment(@PathVariable @Parameter(description = "ID do setor") Long id){
		departmentService.deleteDepartment(securityUtils.getLoggedUserCompany().getId(), id);
		return ResponseEntity.ok().build();
	}

}
