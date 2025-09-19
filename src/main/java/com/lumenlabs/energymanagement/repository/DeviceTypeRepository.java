package com.lumenlabs.energymanagement.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.lumenlabs.energymanagement.model.DeviceType;

public interface DeviceTypeRepository extends JpaRepository<DeviceType, UUID> {
	Page<DeviceType> findAllByCompanyIdAndNameContainingIgnoreCase(UUID companyId, String name, Pageable pageable);
}
