package com.lumenlabs.energymanagement.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.lumenlabs.energymanagement.model.DeviceRoom;

public interface DeviceRoomRepository extends JpaRepository<DeviceRoom, UUID> {

	boolean existsByCompanyIdAndRoomIdAndAliasIgnoreCase(UUID companyId, UUID roomId, String alias);

	Optional<DeviceRoom> findByCompanyIdAndId(UUID companyId, UUID id);

	Page<DeviceRoom> findAllByCompanyIdAndAliasContainingIgnoreCase(UUID companyId, String alias, Pageable pageable);

	Page<DeviceRoom> findAllByRoomIdAndCompanyIdAndAliasContainingIgnoreCase(UUID roomId, UUID companyId, String alias,
			Pageable pageable);

	Page<DeviceRoom> findAllByDeviceIdAndCompanyIdAndRoomNameContainingIgnoreCase(UUID deviceId, UUID companyId,
			String name, Pageable pageable);

	Page<DeviceRoom> findAllByRoomDepartmentIdAndCompanyIdAndAliasContainingIgnoreCase(UUID roomId, UUID companyId,
			String alias, Pageable pageable);

}

