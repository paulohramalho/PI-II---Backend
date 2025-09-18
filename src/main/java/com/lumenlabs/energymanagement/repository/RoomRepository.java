package com.lumenlabs.energymanagement.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.lumenlabs.energymanagement.model.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {

	Page<Room> findAllByDepartmentIdAndCompanyIdAndNameContaining(@Param("departmentId") Long departmentId,
			@Param("companyId") Long companyId, @Param("name") String name, Pageable pageable);
	
	boolean existsByCompanyIdAndName(@Param("companyId") Long companyId, @Param("name") String name);
	
	Optional<Room> findByCompanyIdAndId(Long companyId, Long id);
	
	boolean existsByCompanyIdAndDepartmentIdAndNameAndIdNot(Long companyId, Long departmentId, String name, Long id);

	boolean existsByCompanyIdAndDepartmentIdAndName(Long companyId, Long departmentId, String name);
}
