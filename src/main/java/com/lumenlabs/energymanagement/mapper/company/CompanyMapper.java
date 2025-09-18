package com.lumenlabs.energymanagement.mapper.company;

import org.springframework.stereotype.Component;

import com.lumenlabs.energymanagement.dto.company.CompanyDTO;
import com.lumenlabs.energymanagement.model.Address;
import com.lumenlabs.energymanagement.model.Company;

@Component
public class CompanyMapper {

	public CompanyDTO mapToCompanyDTO(Company company, Address address) {
		CompanyDTO dto = new CompanyDTO();
		
		dto.setBairro(address.getBairro());
		dto.setCity(address.getCity());
		dto.setComplemento(address.getComplemento());
		dto.setLogradouro(address.getLogradouro());
		dto.setNumber(address.getNumber());
		dto.setUf(address.getUf());
		dto.setZipCode(address.getZipCode());
		
		dto.setNomeFantasia(company.getName());
		dto.setRazaoSocial(company.getLegalName());
		dto.setCnpj(company.getCnpj());
		dto.setTelefone(company.getPhoneNumber());

		return dto;
	}
	
}
