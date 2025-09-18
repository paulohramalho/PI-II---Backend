package com.lumenlabs.energymanagement.mapper.company;

import org.springframework.stereotype.Component;

import com.lumenlabs.energymanagement.dto.company.CompanyUpdateDTO;
import com.lumenlabs.energymanagement.model.Address;
import com.lumenlabs.energymanagement.model.Company;

@Component
public class CompanyUpdateMapper {
    
    public void copyToCompany(CompanyUpdateDTO dto, Company company) {
        company.setName(dto.getNomeFantasia());
        company.setPhoneNumber(dto.getTelefone());
    }
    
    public void copyToAddress(CompanyUpdateDTO dto, Address address) {
        address.setLogradouro(dto.getLogradouro());
        address.setNumber(dto.getNumber());
        address.setBairro(dto.getBairro());
        address.setZipCode(dto.getZipCode());
        address.setCity(dto.getCity());
        address.setUf(dto.getUf());
        address.setComplemento(dto.getComplemento());
    }
}
