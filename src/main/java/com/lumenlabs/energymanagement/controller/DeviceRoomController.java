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

import com.lumenlabs.energymanagement.dto.deviceroom.DeviceRoomDTO;
import com.lumenlabs.energymanagement.dto.deviceroom.DeviceRoomRegistrationDTO;
import com.lumenlabs.energymanagement.dto.deviceroom.DeviceRoomUpdateDTO;
import com.lumenlabs.energymanagement.model.DeviceRoom;
import com.lumenlabs.energymanagement.service.DeviceRoomService;
import com.lumenlabs.energymanagement.util.SecurityUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/device-room")
@Tag(name = "Gerenciamento de Vínculos")
public class DeviceRoomController {

	@Autowired
	private DeviceRoomService deviceRoomService;

	@Autowired
	private SecurityUtils securityUtils;

	@Operation(summary = "Listagem de todas as associações")
	@GetMapping
	public ResponseEntity<Page<DeviceRoomDTO>> getRooms(
			@RequestParam(required = false, defaultValue = "") @Parameter(description = "Apelido da Associação") String alias,

			@RequestParam(required = false) @Parameter(description = "ID da Sala") UUID roomId,

			@RequestParam(required = false) @Parameter(description = "ID do Dispositivo") UUID deviceId,

			@PageableDefault(sort = "alias", direction = Direction.ASC) @ParameterObject Pageable pageable) {

		UUID companyId = securityUtils.getLoggedUserCompany().getId();
		return ResponseEntity.ok(deviceRoomService.getAll(companyId, alias, roomId, deviceId, pageable));
	}

	@Operation(summary = "Obter informações de uma associação")
	@GetMapping("/{id}")
	public ResponseEntity<DeviceRoomDTO> getRoom(@PathVariable UUID id) {
		return ResponseEntity.ok(deviceRoomService.getDeviceRoom(securityUtils.getLoggedUserCompany().getId(), id));
	}

	@Operation(summary = "Associar dispositivo a uma sala")
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping
	public ResponseEntity<?> createDeviceRoom(@RequestBody @Valid DeviceRoomRegistrationDTO deviceRoomRegistrationDTO) {
		DeviceRoom deviceRoom = deviceRoomService.createDeviceRoom(securityUtils.getLoggedUserCompany(),
				deviceRoomRegistrationDTO);

		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(deviceRoom.getId())
				.toUri();

		return ResponseEntity.created(location).build();
	}

	@Operation(summary = "Atualizar informações de um vínculo")
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/{id}")
	public ResponseEntity<?> updateRoom(@RequestBody @Valid DeviceRoomUpdateDTO deviceRoomUpdateDTO,
			@PathVariable UUID id) {
		deviceRoomService.updateDeviceRoom(securityUtils.getLoggedUserCompany(), deviceRoomUpdateDTO, id);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "Desassociar dispositivo de uma sala")
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteRoom(@PathVariable UUID id) {
		deviceRoomService.deleteDeviceRoom(securityUtils.getLoggedUserCompany().getId(), id);
		return ResponseEntity.ok().build();
	}

}
