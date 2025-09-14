package com.lumenlabs.energymanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lumenlabs.energymanagement.model.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("SELECT r FROM Room r WHERE r.department.id = :departmentId AND r.department.company.id = :companyId")
    List<Room> findAllByDepartmentIdAndCompanyId(@Param("departmentId") Long departmentId,
                                             @Param("companyId") Long companyId);

    @Query("SELECT r FROM Room r WHERE r.department.company.id = :companyId")
    List<Room> findAllByCompanyId(@Param("companyId") Long companyId);
	
}
