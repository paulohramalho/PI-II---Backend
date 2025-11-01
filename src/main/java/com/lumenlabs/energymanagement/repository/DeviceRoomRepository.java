package com.lumenlabs.energymanagement.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lumenlabs.energymanagement.model.DeviceRoom;

public interface DeviceRoomRepository extends JpaRepository<DeviceRoom, UUID> {
	
	boolean existsByCompanyIdAndRoomIdAndAliasIgnoreCase(UUID companyId, UUID roomId, String alias);

	boolean existsByCompanyIdAndRoomIdAndAliasIgnoreCaseAndIdNot(UUID companyId, UUID roomId, String alias, UUID id);

	Optional<DeviceRoom> findByCompanyIdAndId(UUID companyId, UUID id);

	@Query("""
		    SELECT dr FROM DeviceRoom dr
		    WHERE dr.company.id = :companyId
		      AND (:alias IS NULL OR LOWER(dr.alias) LIKE LOWER(CONCAT('%', :alias, '%')))
		      AND (:roomId IS NULL OR dr.room.id = :roomId)
		      AND (:deviceId IS NULL OR dr.device.id = :deviceId)
		""")
		Page<DeviceRoom> findAllWithFilters(
		    @Param("companyId") UUID companyId,
		    @Param("alias") String alias,
		    @Param("roomId") UUID roomId,
		    @Param("deviceId") UUID deviceId,
		    Pageable pageable
		);


	Page<DeviceRoom> findAllByRoomIdAndCompanyIdAndAliasContainingIgnoreCase(UUID roomId, UUID companyId, String alias,
			Pageable pageable);

	Page<DeviceRoom> findAllByDeviceIdAndCompanyIdAndRoomNameContainingIgnoreCase(UUID deviceId, UUID companyId,
			String name, Pageable pageable);

	Page<DeviceRoom> findAllByRoomDepartmentIdAndCompanyIdAndAliasContainingIgnoreCase(UUID roomId, UUID companyId,
			String alias, Pageable pageable);

	@Query("""
		    SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END
		    FROM Consumption c
		    WHERE c.deviceRoom.id = :deviceRoomId
		      AND c.deviceRoom.company.id = :companyId
		      AND c.eventTime >= :since
		""")
		boolean isDeviceRoomOnline(
		    @Param("deviceRoomId") UUID deviceRoomId,
		    @Param("companyId") UUID companyId,
		    @Param("since") LocalDateTime since
		);
}

