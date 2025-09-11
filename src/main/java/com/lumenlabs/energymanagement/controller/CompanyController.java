package com.lumenlabs.energymanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lumenlabs.energymanagement.dto.company.CompanyRegistrationDTO;
import com.lumenlabs.energymanagement.service.CompanyService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/company")
public class CompanyController {
	
	@Autowired
	private CompanyService companyService;
	
	@PostMapping
	public ResponseEntity<?> registerCompanyWithAdmin(@RequestBody @Valid CompanyRegistrationDTO companyRegistrationDTO){
		companyService.registerCompanyWithAdmin(companyRegistrationDTO);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

}
