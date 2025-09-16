package com.lumenlabs.energymanagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lumenlabs.energymanagement.model.Device;

public interface DeviceRepository extends JpaRepository<Device, Long> {

    @Query("SELECT d FROM Device d " +
            "WHERE d.deviceType.company.id = :companyId")
     Page<Device> findAllByCompany(@Param("companyId") Long companyId, Pageable pageable);

     @Query("SELECT d FROM Device d " +
            "WHERE d.deviceType.id = :deviceTypeId " +
            "AND d.deviceType.company.id = :companyId")
     Page<Device> findAllByDeviceTypeAndCompany(@Param("deviceTypeId") Long deviceTypeId,
                                                @Param("companyId") Long companyId,
                                                Pageable pageable);

    @Query("SELECT d FROM Device d " +
    	       "WHERE d.name = :name AND d.deviceType.id = :deviceTypeId")
    	Page<Device> findAllByNameAndDeviceType(@Param("name") String name,
    	                                        @Param("deviceTypeId") Long deviceTypeId,
    	                                        Pageable pageable);
}
