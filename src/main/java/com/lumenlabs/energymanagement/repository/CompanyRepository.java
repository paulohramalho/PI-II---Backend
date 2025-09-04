package com.lumenlabs.energymanagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lumenlabs.energymanagement.model.Company;

public interface CompanyRepository extends JpaRepository<Company, Long>{

	Optional<Company> findByLegalName(String razaoSocial);

}
