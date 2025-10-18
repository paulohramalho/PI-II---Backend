package com.lumenlabs.energymanagement.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.lumenlabs.energymanagement.model.Room;

public interface RoomRepository extends JpaRepository<Room, UUID> {

	Page<Room> findAllByDepartmentIdAndCompanyIdAndNameContainingIgnoreCase(UUID departmentId, UUID companyId,
			String name, Pageable pageable);

	boolean existsByCompanyIdAndNameIgnoreCase(UUID companyId, String name);

	Optional<Room> findByCompanyIdAndId(UUID companyId, UUID id);

	boolean existsByCompanyIdAndDepartmentIdAndNameIgnoreCaseAndIdNot(UUID companyId, UUID departmentId, String name,
			UUID id);

	boolean existsByCompanyIdAndDepartmentIdAndNameIgnoreCase(UUID companyId, UUID departmentId, String name);

	Page<Room> findAllByCompanyIdAndNameContainingIgnoreCase(UUID companyId, String name, Pageable pageable);

	boolean existsByCompanyIdAndId(UUID companyId, UUID roomId);

	@Query("""
			SELECT r
			FROM Room r
			JOIN DeviceRoom dr ON dr.room = r
			WHERE dr.device.id = :deviceId
			  AND r.company.id = :companyId
			  AND LOWER(r.name) LIKE LOWER(CONCAT('%', :nameFilter, '%'))
			""")
	Page<Room> findRoomsByDevice(UUID deviceId, UUID companyId, String nameFilter, Pageable pageable);

}
