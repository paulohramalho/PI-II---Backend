package com.lumenlabs.energymanagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lumenlabs.energymanagement.model.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("SELECT r FROM Room r " +
           "WHERE r.department.id = :departmentId " +
           "AND r.department.company.id = :companyId " +
           "AND (:name IS NULL OR r.name LIKE :name)")
    Page<Room> findAllByDepartmentIdAndCompanyIdAndName(@Param("departmentId") Long departmentId,
                                                        @Param("companyId") Long companyId,
                                                        @Param("name") String name,
                                                        Pageable pageable);

    @Query("SELECT r FROM Room r " +
           "WHERE r.department.company.id = :companyId " +
           "AND (:name IS NULL OR r.name LIKE :name)")
    Page<Room> findAllByCompanyIdAndName(@Param("companyId") Long companyId,
                                         @Param("name") String name,
                                         Pageable pageable);
}

