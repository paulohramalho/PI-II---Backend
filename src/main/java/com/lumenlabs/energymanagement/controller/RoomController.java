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

import com.lumenlabs.energymanagement.dto.room.RoomDTO;
import com.lumenlabs.energymanagement.dto.room.RoomRegistrationDTO;
import com.lumenlabs.energymanagement.dto.room.RoomUpdateDTO;
import com.lumenlabs.energymanagement.model.Room;
import com.lumenlabs.energymanagement.service.RoomService;
import com.lumenlabs.energymanagement.util.SecurityUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/room")
@Tag(name = "Sala")
public class RoomController {
	
	@Autowired
	private RoomService roomService;
	
	@Autowired
	private SecurityUtils securityUtils;
	
	@Operation(summary = "Listagem de todas as Salas")
	@GetMapping
	public ResponseEntity<Page<RoomDTO>> getRooms(@RequestParam(required = false, defaultValue = "") @Parameter(description = "Nome da Sala") String name, 
			@PageableDefault(sort = "name", direction = Direction.ASC) @ParameterObject Pageable pageable){
		return ResponseEntity.ok(roomService.getAll(securityUtils.getLoggedUserCompany().getId(), name, pageable));
	}
	
	@Operation(summary = "Obter informacao da Sala")
	@GetMapping("/{id}")
	public ResponseEntity<RoomDTO> getRoom(@PathVariable UUID id){
		return ResponseEntity.ok(roomService.getRoom(securityUtils.getLoggedUserCompany().getId(), id));
	}
	
	@Operation(summary = "Cadastro de Sala")
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping
	public ResponseEntity<?> createRoom(@RequestBody @Valid RoomRegistrationDTO roomRegistrationDTO){
		Room room = roomService.createRoom(securityUtils.getLoggedUserCompany(), roomRegistrationDTO);
		
		URI location = ServletUriComponentsBuilder
	            .fromCurrentRequest()
	            .path("/{id}")
	            .buildAndExpand(room.getId())
	            .toUri();

	    return ResponseEntity.created(location).build();
	}

	@Operation(summary = "Atualizar informacoes da sala")
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/{id}")
	public ResponseEntity<?> updateRoom(@RequestBody @Valid RoomUpdateDTO roomUpdateDTO, @PathVariable UUID id){
		roomService.updateRoom(securityUtils.getLoggedUserCompany(), roomUpdateDTO, id);
		return ResponseEntity.ok().build();
	}
	
	@Operation(summary = "Remover Sala")
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteRoom(@PathVariable UUID id){
		roomService.deleteRoom(securityUtils.getLoggedUserCompany().getId(), id);
		return ResponseEntity.ok().build();
	}
}
