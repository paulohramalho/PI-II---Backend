package com.lumenlabs.energymanagement.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lumenlabs.energymanagement.model.DeviceRoom;
import com.lumenlabs.energymanagement.model.embedded.RoomDeviceId;

public interface DeviceRoomRepository extends JpaRepository<DeviceRoom, RoomDeviceId> {

    @Query("SELECT dr FROM DeviceRoom dr " +
           "WHERE dr.room.department.company.id = :companyId " +
           "AND (:deviceName IS NULL OR dr.device.name LIKE %:deviceName%)")
    Page<DeviceRoom> findAllByCompanyId(@Param("companyId") UUID companyId,
                                        @Param("deviceName") String deviceName,
                                        Pageable pageable);

    @Query("SELECT dr FROM DeviceRoom dr " +
           "WHERE dr.room.id = :roomId " +
           "AND dr.room.department.company.id = :companyId " +
           "AND (:deviceName IS NULL OR dr.device.name LIKE %:deviceName%)")
    Page<DeviceRoom> findAllByRoomIdAndCompanyId(@Param("roomId") UUID roomId,
                                                 @Param("companyId") UUID companyId,
                                                 @Param("deviceName") String deviceName,
                                                 Pageable pageable);

    @Query("SELECT dr FROM DeviceRoom dr " +
           "WHERE dr.room.department.id = :departmentId " +
           "AND dr.room.department.company.id = :companyId " +
           "AND (:deviceName IS NULL OR dr.device.name LIKE %:deviceName%)")
    Page<DeviceRoom> findAllByDepartmentIdAndCompanyId(@Param("departmentId") UUID departmentId,
                                                       @Param("companyId") UUID companyId,
                                                       @Param("deviceName") String deviceName,
                                                       Pageable pageable);
}

