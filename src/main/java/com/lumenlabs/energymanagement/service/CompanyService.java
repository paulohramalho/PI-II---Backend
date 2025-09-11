package com.lumenlabs.energymanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.lumenlabs.energymanagement.dto.company.CompanyRegistrationDTO;
import com.lumenlabs.energymanagement.mapper.company.CompanyRegistrationMapper;
import com.lumenlabs.energymanagement.model.Address;
import com.lumenlabs.energymanagement.model.Company;
import com.lumenlabs.energymanagement.model.User;
import com.lumenlabs.energymanagement.repository.AddressRepository;
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
	private AddressRepository addressRepository;
	@Autowired
	private CompanyRegistrationMapper companyRegistrationMapper;

	@Transactional
	public Company registerCompanyWithAdmin(CompanyRegistrationDTO dto) {
		userRepository.findByEmail(dto.getAdminEmail())
	    .ifPresent(u -> {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email do administrador já registrado");
	    });
		
		if(companyRepository.existsByLegalNameAndCnpj(dto.getRazaoSocial(), dto.getCnpj())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Razão Social já registrada para esse CNPJ");
		}
		
		if(companyRepository.existsByCnpj(dto.getCnpj())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CNPJ já registrado");
		}

		Company company = companyRegistrationMapper.mapToCompany(dto);
		companyRepository.save(company);
		Address address = companyRegistrationMapper.mapToAddress(dto, company);
		addressRepository.save(address);
		User admin = companyRegistrationMapper.mapToAdminUser(dto, company);;
		userRepository.save(admin);

		return company;
	}

}
