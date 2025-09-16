package com.lumenlabs.energymanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lumenlabs.energymanagement.model.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {
	
}
