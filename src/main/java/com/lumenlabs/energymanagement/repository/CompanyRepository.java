package com.lumenlabs.energymanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lumenlabs.energymanagement.model.Company;

public interface CompanyRepository extends JpaRepository<Company, Long>{

	Boolean existsByLegalName(String razaoSocial);
	Boolean existsByCnpj(String cnpj);

}
