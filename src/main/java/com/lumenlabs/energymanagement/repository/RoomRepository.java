package com.lumenlabs.energymanagement.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.lumenlabs.energymanagement.model.Room;

public interface RoomRepository extends JpaRepository<Room, UUID> {

	Page<Room> findAllByDepartmentIdAndCompanyIdAndNameContainingIgnoreCase(UUID departmentId, UUID companyId, String name, Pageable pageable);
	
	boolean existsByCompanyIdAndName(UUID companyId, String name);
	
	Optional<Room> findByCompanyIdAndId(UUID companyId, UUID id);
	
	boolean existsByCompanyIdAndDepartmentIdAndNameAndIdNot(UUID companyId, UUID departmentId, String name, UUID id);

	boolean existsByCompanyIdAndDepartmentIdAndName(UUID companyId, UUID departmentId, String name);
}
