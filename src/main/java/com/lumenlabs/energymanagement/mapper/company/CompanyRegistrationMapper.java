package com.lumenlabs.energymanagement.mapper.company;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.lumenlabs.energymanagement.dto.company.CompanyRegistrationDTO;
import com.lumenlabs.energymanagement.enums.Role;
import com.lumenlabs.energymanagement.model.Address;
import com.lumenlabs.energymanagement.model.Company;
import com.lumenlabs.energymanagement.model.User;

@Component
public class CompanyRegistrationMapper {

	@Autowired
	private PasswordEncoder passwordEncoder;

    public Company mapToCompany(CompanyRegistrationDTO dto) {
        Company company = new Company();
        company.setCnpj(dto.getCnpj());
        company.setLegalName(dto.getRazaoSocial());
        company.setName(dto.getNomeFantasia());
        company.setPhoneNumber(dto.getTelefone());
        return company;
    }

    public Address mapToAddress(CompanyRegistrationDTO dto, Company company) {
        Address address = new Address();
        address.setCompany(company);
        address.setLogradouro(dto.getLogradouro());
        address.setNumber(dto.getNumber());
        address.setBairro(dto.getBairro());
        address.setZipCode(dto.getZipCode());
        address.setCity(dto.getCity());
        address.setUf(dto.getUf());
        address.setComplemento(dto.getComplemento());
        return address;
    }

    public User mapToAdminUser(CompanyRegistrationDTO dto, Company company) {
        User user = new User();
        user.setEmail(dto.getAdminEmail());
        user.setPassword(passwordEncoder.encode(dto.getAdminPassword()));
        user.setRole(Role.ADMIN);
        user.setCompany(company);
        user.setName(dto.getAdminName());
        return user;
    }
	
}
