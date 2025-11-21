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

import com.lumenlabs.energymanagement.dto.user.UserDTO;
import com.lumenlabs.energymanagement.dto.user.UserRegistrationDTO;
import com.lumenlabs.energymanagement.model.User;
import com.lumenlabs.energymanagement.service.UserService;
import com.lumenlabs.energymanagement.util.SecurityUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/user")
@Tag(name = "Usuário")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private SecurityUtils securityUtils;

	@Operation(summary = "Listagem de todos os usuários")
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping
	public ResponseEntity<Page<UserDTO>> getUsers(
			@RequestParam(required = false, defaultValue = "") @Parameter(description = "Nome do Usuário") String name,
			@PageableDefault(sort = "name", direction = Direction.ASC) @ParameterObject Pageable pageable) {
		return ResponseEntity.ok(userService.getUsers(securityUtils.getLoggedUserCompany().getId(), name, pageable));
	}
	
	@Operation(summary = "Obter informações de usuário")
	@GetMapping("/{id}")
	public ResponseEntity<UserDTO> getUser(@PathVariable @Parameter(description = "ID do Usuário")  UUID id){
		return ResponseEntity.ok(userService.getUser(securityUtils.getLoggedUserCompany().getId(), id));
	}
	
	@Operation(summary = "Criar novo usuário")
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping
	public ResponseEntity<?> createUser(@RequestBody @Valid UserRegistrationDTO userRegistrationDTO){
		User user = userService.createUser(securityUtils.getLoggedUserCompany(), userRegistrationDTO);
		
		URI location = ServletUriComponentsBuilder
	            .fromCurrentRequest()
	            .path("/{id}")
	            .buildAndExpand(user.getId())
	            .toUri();

	    return ResponseEntity.created(location).build();
	}
	
	@Operation(summary = "Atualizar informacoes do usuário")
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/{id}")
	public ResponseEntity<?> updateUser(@RequestBody @Valid UserRegistrationDTO userRegistrationDTO, @PathVariable @Parameter(description = "ID do usuário") UUID id){
		userService.updateUser(securityUtils.getLoggedUserCompany(), userRegistrationDTO, id);
		return ResponseEntity.ok().build();
	}
	
	@Operation(summary = "Remover usuário")
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable @Parameter(description = "ID do usuário") UUID id){
		userService.deleteUser(securityUtils.getLoggedUserCompany().getId(), id);
		return ResponseEntity.ok().build();
	}

}
