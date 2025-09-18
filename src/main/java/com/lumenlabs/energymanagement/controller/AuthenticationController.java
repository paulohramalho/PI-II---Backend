package com.lumenlabs.energymanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lumenlabs.energymanagement.dto.security.LoginDTO;
import com.lumenlabs.energymanagement.dto.security.TokenDTO;
import com.lumenlabs.energymanagement.service.JWTService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/login")
@Tag(name = "Auth")
public class AuthenticationController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JWTService jwtService;

	@PostMapping
	public ResponseEntity<TokenDTO> login(@RequestBody @Valid LoginDTO loginDTO) {
		try {
			UsernamePasswordAuthenticationToken userPassAuthToken = new UsernamePasswordAuthenticationToken(
					loginDTO.getEmail(), loginDTO.getPassword());
			authenticationManager.authenticate(userPassAuthToken);
			return ResponseEntity.status(HttpStatus.OK).body(new TokenDTO(jwtService.getToken(loginDTO)));
		} catch (BadCredentialsException ex) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

}
