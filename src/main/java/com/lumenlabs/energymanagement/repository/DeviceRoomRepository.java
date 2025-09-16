package com.lumenlabs.energymanagement.repository;

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
    Page<DeviceRoom> findAllByCompanyId(@Param("companyId") Long companyId,
                                        @Param("deviceName") String deviceName,
                                        Pageable pageable);

    @Query("SELECT dr FROM DeviceRoom dr " +
           "WHERE dr.room.id = :roomId " +
           "AND dr.room.department.company.id = :companyId " +
           "AND (:deviceName IS NULL OR dr.device.name LIKE %:deviceName%)")
    Page<DeviceRoom> findAllByRoomIdAndCompanyId(@Param("roomId") Long roomId,
                                                 @Param("companyId") Long companyId,
                                                 @Param("deviceName") String deviceName,
                                                 Pageable pageable);

    @Query("SELECT dr FROM DeviceRoom dr " +
           "WHERE dr.room.department.id = :departmentId " +
           "AND dr.room.department.company.id = :companyId " +
           "AND (:deviceName IS NULL OR dr.device.name LIKE %:deviceName%)")
    Page<DeviceRoom> findAllByDepartmentIdAndCompanyId(@Param("departmentId") Long departmentId,
                                                       @Param("companyId") Long companyId,
                                                       @Param("deviceName") String deviceName,
                                                       Pageable pageable);
}

