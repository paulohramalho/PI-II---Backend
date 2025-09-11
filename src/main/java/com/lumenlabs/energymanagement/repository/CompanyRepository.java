package com.lumenlabs.energymanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lumenlabs.energymanagement.model.Company;

public interface CompanyRepository extends JpaRepository<Company, Long>{

	Boolean existsByLegalNameAndCnpj(String razaoSocial, String cnpj);
	Boolean existsByCnpj(String cnpj);

}
