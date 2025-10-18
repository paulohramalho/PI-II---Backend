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

import com.lumenlabs.energymanagement.dto.device.DeviceDTO;
import com.lumenlabs.energymanagement.dto.device.DeviceRegistrationDTO;
import com.lumenlabs.energymanagement.dto.device.DeviceUpdateDTO;
import com.lumenlabs.energymanagement.dto.room.RoomDTO;
import com.lumenlabs.energymanagement.model.Device;
import com.lumenlabs.energymanagement.service.DeviceRoomService;
import com.lumenlabs.energymanagement.service.DeviceService;
import com.lumenlabs.energymanagement.util.SecurityUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/device")
@Tag(name = "Dispositivo")
public class DeviceController {

	@Autowired
	private DeviceService deviceService;
	
	@Autowired
	private DeviceRoomService deviceRoomService;
	
	@Autowired
	private SecurityUtils securityUtils;
	
	@Operation(summary = "Listagem de todas as salas associadas ao dispositivo")
	@GetMapping("/{id}/room")
	public ResponseEntity<Page<RoomDTO>> getRoomsByDevice(@RequestParam(required = false, defaultValue = "") @Parameter(description = "Nome da sala") String name, 
			@PathVariable UUID id
			,@PageableDefault(sort = "alias", direction = Direction.ASC) @ParameterObject Pageable pageable){
		return ResponseEntity.ok(deviceRoomService.getRoomsByDevice(securityUtils.getLoggedUserCompany().getId(), id, name, pageable));
	}
	
	@Operation(summary = "Listagem de todos os Dispositivos")
	@GetMapping
	public ResponseEntity<Page<DeviceDTO>> getDevices(@RequestParam(required = false, defaultValue = "") @Parameter(description = "Nome da Sala") String name, 
			@PageableDefault(sort = "name", direction = Direction.ASC) @ParameterObject Pageable pageable){
		return ResponseEntity.ok(deviceService.getAll(securityUtils.getLoggedUserCompany().getId(), name, pageable));
	}
	
	@Operation(summary = "Obter informacao do Dispositivo")
	@GetMapping("/{id}")
	public ResponseEntity<DeviceDTO> getDevice(@PathVariable UUID id){
		return ResponseEntity.ok(deviceService.getDevice(securityUtils.getLoggedUserCompany().getId(), id));
	}
	
	@Operation(summary = "Cadastro de Dispositivo")
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping
	public ResponseEntity<?> createDevice(@RequestBody @Valid DeviceRegistrationDTO deviceRegistrationDTO){
		Device device = deviceService.createDevice(securityUtils.getLoggedUserCompany(), deviceRegistrationDTO);
		
		URI location = ServletUriComponentsBuilder
	            .fromCurrentRequest()
	            .path("/{id}")
	            .buildAndExpand(device.getId())
	            .toUri();

	    return ResponseEntity.created(location).build();
	}

	@Operation(summary = "Atualizar informacoes do Dispositivo")
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/{id}")
	public ResponseEntity<?> updateDevice(@RequestBody @Valid DeviceUpdateDTO deviceUpdateDTO, @PathVariable UUID id){
		deviceService.updateDevice(securityUtils.getLoggedUserCompany(), deviceUpdateDTO, id);
		return ResponseEntity.ok().build();
	}
	
	@Operation(summary = "Remover Dispositivo")
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteDevice(@PathVariable UUID id){
		deviceService.deleteDevice(securityUtils.getLoggedUserCompany().getId(), id);
		return ResponseEntity.ok().build();
	}
	
}
