package com.lumenlabs.energymanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lumenlabs.energymanagement.dto.company.CompanyDTO;
import com.lumenlabs.energymanagement.dto.company.CompanyRegistrationDTO;
import com.lumenlabs.energymanagement.dto.company.CompanyUpdateDTO;
import com.lumenlabs.energymanagement.service.CompanyService;
import com.lumenlabs.energymanagement.util.SecurityUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/company")
@Tag(name = "Empresa")
public class CompanyController {
	
	@Autowired
	private CompanyService companyService;
	
	@Autowired
	private SecurityUtils securityUtils;
	
	@Operation(summary = "Obter informcoes sobre a empresa")
	@GetMapping
	public ResponseEntity<CompanyDTO> getCompany(){
		return ResponseEntity.ok(companyService.getCompany(securityUtils.getLoggedUserCompany().getId()));
	}
	
	@Operation(summary = "Cadastrar empresa e usuario administrador")
	@PostMapping
	public ResponseEntity<?> registerCompanyWithAdmin(@RequestBody @Valid CompanyRegistrationDTO companyRegistrationDTO){
		companyService.registerCompanyWithAdmin(companyRegistrationDTO);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	
	@Operation(summary = "Atualizar dados cadastrais da empresa")
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping
	public ResponseEntity<?> updateCompanyWithAdmin(@RequestBody @Valid CompanyUpdateDTO companyUpdateDTO){
		companyService.updateCompanyWithAdmin(companyUpdateDTO, securityUtils.getLoggedUserCompany().getId());
		return ResponseEntity.ok().build();
	}
	
	@Operation(summary = "Deletar empresa")
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping
	public ResponseEntity<?> deleteCompanyWithAdmin(){
		companyService.deleteCompanyWithAdmin(securityUtils.getLoggedUserCompany().getId());
		return ResponseEntity.ok().build();
	}

}
