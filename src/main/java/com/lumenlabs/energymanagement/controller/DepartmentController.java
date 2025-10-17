package com.lumenlabs.energymanagement.controller;

import java.net.URI;
import java.util.UUID;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
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

import io.swagger.v3.oas.annotations.Operation;
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
	
	@Operation(summary = "Obter informcoes sobre o setor")
	@GetMapping("/{id}")
	public ResponseEntity<DepartmentDTO> getDepartment(@PathVariable @Parameter(description = "ID do setor")  UUID id){
		return ResponseEntity.ok(departmentService.getDepartment(securityUtils.getLoggedUserCompany().getId(), id));
	}
	
	@Operation(summary = "Consultar salas atreladas ao setor")
	@GetMapping("/{id}/room")
	public ResponseEntity<Page<RoomDTO>> getRooms(@PathVariable @Parameter(description = "ID do setor")  UUID id, 
			@RequestParam(required = false, defaultValue = "") @Parameter(description = "Nome da Sala") String name, 
			@PageableDefault(sort = "name", direction = Direction.ASC) @ParameterObject Pageable pageable){
		return ResponseEntity.ok(roomService.getRoomByDepartment(securityUtils.getLoggedUserCompany().getId(), id, name, pageable));
	}
	
	@Operation(summary = "Listagem de todos os setores")
	@GetMapping
	public ResponseEntity<Page<DepartmentDTO>> getAll(@RequestParam(required = false, defaultValue = "") @Parameter(description = "Nome do Setor") String name, 
			@PageableDefault(sort = "name", direction = Direction.ASC) @ParameterObject Pageable pageable){
		return ResponseEntity.ok(departmentService.getAll(securityUtils.getLoggedUserCompany().getId(), name, pageable));
	}
	
	@Operation(summary = "Cadastro de setor")
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
	
	@Operation(summary = "Atualizar informacoes sobre o setor")
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/{id}")
	public ResponseEntity<?> updateDepartment(@RequestBody @Valid DepartmentRegistrationDTO departmentRegistrationDTO, @PathVariable @Parameter(description = "ID do setor") UUID id){
		departmentService.updateDepartment(securityUtils.getLoggedUserCompany(), departmentRegistrationDTO, id);
		return ResponseEntity.ok().build();
	}
	
	@Operation(summary = "Remover setor")
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteDepartment(@PathVariable @Parameter(description = "ID do setor") UUID id){
		departmentService.deleteDepartment(securityUtils.getLoggedUserCompany().getId(), id);
		return ResponseEntity.ok().build();
	}

}
