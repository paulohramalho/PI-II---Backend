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

import com.lumenlabs.energymanagement.dto.devicetype.DeviceTypeDTO;
import com.lumenlabs.energymanagement.dto.devicetype.DeviceTypeRegistrationDTO;
import com.lumenlabs.energymanagement.model.DeviceType;
import com.lumenlabs.energymanagement.service.DeviceTypeService;
import com.lumenlabs.energymanagement.util.SecurityUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/device-type")
@Tag(name = "Tipo Dispositivo")
public class DeviceTypeController {
	
	@Autowired
	private DeviceTypeService deviceTypeService;
	
	@Autowired
	private SecurityUtils securityUtils;
	
	@Operation(summary = "Listagem de todos os Tipos Dispositivos")
	@GetMapping
	public ResponseEntity<Page<DeviceTypeDTO>> getAll(@RequestParam(required = false, defaultValue = "") @Parameter(description = "Nome do Setor") String name, 
			@PageableDefault(sort = "name", direction = Direction.ASC) @ParameterObject Pageable pageable){
		return ResponseEntity.ok(deviceTypeService.getAll(securityUtils.getLoggedUserCompany().getId(), name, pageable));
	}
	
	@Operation(summary = "Obter informacao do Tipo Dispositivo")
	@GetMapping("/{id}")
	public ResponseEntity<DeviceTypeDTO> getDeviceType(@PathVariable @Parameter(description = "ID do setor")  UUID id){
		return ResponseEntity.ok(deviceTypeService.getDeviceType(securityUtils.getLoggedUserCompany().getId(), id));
	}
	
	@Operation(summary = "Cadastro de Tipo Dispositivo")
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping
	public ResponseEntity<?> createDeviceType(@RequestBody @Valid DeviceTypeRegistrationDTO deviceTypeRegistrationDTO){
		DeviceType deviceType = deviceTypeService.create(securityUtils.getLoggedUserCompany(), deviceTypeRegistrationDTO);
		
		URI location = ServletUriComponentsBuilder
	            .fromCurrentRequest()
	            .path("/{id}")
	            .buildAndExpand(deviceType.getId())
	            .toUri();

	    return ResponseEntity.created(location).build();
	}
	
	@Operation(summary = "Atualizar informacoes do Tipo Dispositivo")
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/{id}")
	public ResponseEntity<?> updateDeviceType(@RequestBody @Valid DeviceTypeRegistrationDTO deviceTypeRegistrationDTO, 
			@PathVariable @Parameter(description = "ID do tipo dispositivo") UUID id){
		deviceTypeService.updateDeviceType(securityUtils.getLoggedUserCompany(), deviceTypeRegistrationDTO, id);
		return ResponseEntity.ok().build();
	}
	
	@Operation(summary = "Remover Tipo Dispositivo")
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteDeviceType(@PathVariable @Parameter(description = "ID do tipo dispositivo") UUID id){
		deviceTypeService.deleteDeviceType(securityUtils.getLoggedUserCompany().getId(), id);
		return ResponseEntity.ok().build();
	}
	
}
