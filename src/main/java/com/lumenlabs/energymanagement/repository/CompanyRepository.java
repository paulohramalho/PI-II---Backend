package com.lumenlabs.energymanagement.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lumenlabs.energymanagement.model.Company;

public interface CompanyRepository extends JpaRepository<Company, UUID>{

	Boolean existsByLegalNameAndCnpj(String razaoSocial, String cnpj);
	Boolean existsByCnpj(String cnpj);

}
