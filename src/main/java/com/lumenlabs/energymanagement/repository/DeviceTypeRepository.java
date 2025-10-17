package com.lumenlabs.energymanagement.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.lumenlabs.energymanagement.model.DeviceType;

public interface DeviceTypeRepository extends JpaRepository<DeviceType, UUID> {

	boolean existsByCompanyIdAndNameIgnoreCase(UUID companyId, String name);

	Page<DeviceType> findAllByCompanyIdAndNameContainingIgnoreCase(UUID companyId, String name, Pageable pageable);

	Optional<DeviceType> findByCompanyIdAndId(UUID companyId, UUID deviceTypeId);

	boolean existsByCompanyIdAndNameIgnoreCaseAndIdNot(UUID companyId, String name, UUID id);
}
