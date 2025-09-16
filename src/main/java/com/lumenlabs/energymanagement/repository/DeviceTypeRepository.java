package com.lumenlabs.energymanagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.lumenlabs.energymanagement.model.DeviceType;

public interface DeviceTypeRepository extends JpaRepository<DeviceType, Long> {
	Page<DeviceType> findAllByCompanyIdAndNameContaining(Long companyId, String name, Pageable pageable);
}
