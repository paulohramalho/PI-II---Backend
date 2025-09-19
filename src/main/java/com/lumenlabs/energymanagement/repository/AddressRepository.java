package com.lumenlabs.energymanagement.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lumenlabs.energymanagement.model.Address;

public interface AddressRepository extends JpaRepository<Address, UUID> {

	Optional<Address> findByCompanyId(UUID companyId);
	
}
