package com.lumenlabs.energymanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lumenlabs.energymanagement.model.DeviceType;

public interface DeviceTypeRepository extends JpaRepository<DeviceType, Long> {
	List<DeviceType> findAllByCompanyId(Long companyId);
}
