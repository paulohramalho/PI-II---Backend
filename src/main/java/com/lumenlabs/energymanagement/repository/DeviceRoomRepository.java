package com.lumenlabs.energymanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lumenlabs.energymanagement.model.DeviceRoom;
import com.lumenlabs.energymanagement.model.embedded.RoomDeviceId;

public interface DeviceRoomRepository extends JpaRepository<DeviceRoom, RoomDeviceId> {

	@Query("SELECT dr FROM DeviceRoom dr " + "WHERE dr.room.department.company.id = :companyId")
	List<DeviceRoom> findAllByCompanyId(@Param("companyId") Long companyId);

	@Query("SELECT dr FROM DeviceRoom dr "
			+ "WHERE dr.room.id = :roomId AND dr.room.department.company.id = :companyId")
	List<DeviceRoom> findAllByRoomIdAndCompanyId(@Param("roomId") Long roomId, @Param("companyId") Long companyId);

}
