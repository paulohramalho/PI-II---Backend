package com.lumenlabs.energymanagement.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lumenlabs.energymanagement.model.Device;

public interface DeviceRepository extends JpaRepository<Device, UUID> {

    @Query("SELECT d FROM Device d " +
            "WHERE d.deviceType.company.id = :companyId")
     Page<Device> findAllByCompany(@Param("companyId") UUID companyId, Pageable pageable);

     @Query("SELECT d FROM Device d " +
            "WHERE d.deviceType.id = :deviceTypeId " +
            "AND d.deviceType.company.id = :companyId")
     Page<Device> findAllByDeviceTypeAndCompany(@Param("deviceTypeId") UUID deviceTypeId,
                                                @Param("companyId") UUID companyId,
                                                Pageable pageable);

    @Query("SELECT d FROM Device d " +
    	       "WHERE d.name = :name AND d.deviceType.id = :deviceTypeId")
    	Page<Device> findAllByNameAndDeviceType(@Param("name") String name,
    	                                        @Param("deviceTypeId") UUID deviceTypeId,
    	                                        Pageable pageable);

	boolean existsByCompanyIdAndNameIgnoreCase(UUID companyId, String name);

	Optional<Device> findByCompanyIdAndId(UUID companyId, UUID id);

	boolean existsByCompanyIdAndNameIgnoreCaseAndIdNot(UUID companyId, String name, UUID id);

	Page<Device> findAllByCompanyIdAndNameContainingIgnoreCase(UUID companyId, String name, Pageable pageable);

	Page<Device> findAllByDeviceTypeIdAndCompanyIdAndNameContainingIgnoreCase(UUID deviceTypeId, UUID companyId,
			String name, Pageable pageable);
}
