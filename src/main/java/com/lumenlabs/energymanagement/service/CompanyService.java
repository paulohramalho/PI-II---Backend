package com.lumenlabs.energymanagement.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.lumenlabs.energymanagement.dto.company.CompanyDTO;
import com.lumenlabs.energymanagement.dto.company.CompanyRegistrationDTO;
import com.lumenlabs.energymanagement.dto.company.CompanyUpdateDTO;
import com.lumenlabs.energymanagement.mapper.company.CompanyMapper;
import com.lumenlabs.energymanagement.mapper.company.CompanyRegistrationMapper;
import com.lumenlabs.energymanagement.mapper.company.CompanyUpdateMapper;
import com.lumenlabs.energymanagement.model.Address;
import com.lumenlabs.energymanagement.model.Company;
import com.lumenlabs.energymanagement.model.User;
import com.lumenlabs.energymanagement.repository.AddressRepository;
import com.lumenlabs.energymanagement.repository.CompanyRepository;
import com.lumenlabs.energymanagement.repository.UserRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

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
	@Autowired
	private CompanyUpdateMapper companyUpdateMapper;
	@Autowired
	private CompanyMapper companyMapper;

	@Transactional
	public Company registerCompanyWithAdmin(CompanyRegistrationDTO dto) {
		basicValidation(dto);

		Company company = companyRegistrationMapper.mapToCompany(dto);
		companyRepository.save(company);
		Address address = companyRegistrationMapper.mapToAddress(dto, company);
		addressRepository.save(address);
		User admin = companyRegistrationMapper.mapToAdminUser(dto, company);
		userRepository.save(admin);

		return company;
	}

	@Transactional
	public void updateCompanyWithAdmin(@Valid CompanyUpdateDTO dto, UUID id) {
		Company company = companyRepository.findById(id).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa não encontrada"));
		Address address = addressRepository.findByCompanyId(id).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Endereço não encontrado"));
		companyUpdateMapper.copyToCompany(dto, company);
		companyRepository.save(company);
		companyUpdateMapper.copyToAddress(dto, address);
		addressRepository.save(address);
	}

	private void basicValidation(CompanyRegistrationDTO dto) {
		userRepository.findByEmailIgnoreCase(dto.getAdminEmail()).ifPresent(u -> {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Email do administrador já registrado");
		});

		if (companyRepository.existsByLegalNameAndCnpj(dto.getRazaoSocial(), dto.getCnpj())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Razão Social já registrada para esse CNPJ");
		}

		if (companyRepository.existsByCnpj(dto.getCnpj())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "CNPJ já registrado");
		}
	}

	@Transactional
	public void deleteCompanyWithAdmin(UUID id) {
		if(!companyRepository.existsById(id))
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa não encontrada");
		companyRepository.deleteById(id);
	}

	public CompanyDTO getCompany(UUID id) {
		Company company = companyRepository.findById(id).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa não encontrada"));
		Address address = addressRepository.findByCompanyId(id).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Endereço não encontrado"));
		return companyMapper.mapToCompanyDTO(company, address);
	}
}
