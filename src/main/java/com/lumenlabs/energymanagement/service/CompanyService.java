package com.lumenlabs.energymanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.lumenlabs.energymanagement.dto.security.CompanyRegistrationDTO;
import com.lumenlabs.energymanagement.enums.Role;
import com.lumenlabs.energymanagement.model.Company;
import com.lumenlabs.energymanagement.model.User;
import com.lumenlabs.energymanagement.repository.CompanyRepository;
import com.lumenlabs.energymanagement.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class CompanyService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private CompanyRepository companyRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Transactional
	public Company registerCompanyWithAdmin(CompanyRegistrationDTO dto) {
		if (userRepository.findByEmail(dto.getAdminEmail()).isPresent()) {
			throw new  ResponseStatusException(HttpStatus.BAD_REQUEST, "Email do administrador já registrado");
		}
		
		if(companyRepository.findByLegalName(dto.getRazaoSocial()).isPresent()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Razão Social já registrada");
		}

		Company company = new Company();
		company.setLegalName(dto.getRazaoSocial());
		company.setName(dto.getNomeFantasia());
		companyRepository.save(company);

		User admin = new User();
		admin.setEmail(dto.getAdminEmail());
		admin.setPassword(passwordEncoder.encode(dto.getAdminPassword()));
		admin.setRole(Role.ADMIN);
		admin.setCompany(company);
		userRepository.save(admin);

		return company;
	}

}
