package com.lumenlabs.energymanagement.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lumenlabs.energymanagement.model.Consumption;

@Repository
public interface ConsumptionRepository extends JpaRepository<Consumption, UUID> {

    // ========================================
    // EVOLUTION - DEPARTMENT (SETOR)
    // ========================================
    
    @Query(value = """
        SELECT 
            chd.hour as timestamp,
            chd.fk_setor as entity_id,
            s.nome as name,
            chd.total_potencia,
            chd.avg_potencia,
            chd.max_potencia,
            chd.min_potencia
        FROM consumo_hourly_department chd
        JOIN setor s ON chd.fk_setor = s.id
        WHERE chd.fk_empresa = :empresaId
            AND chd.hour >= :startDate
            AND chd.hour < :endDate
        ORDER BY chd.hour, s.nome
        """, nativeQuery = true)
    List<Object[]> findDepartmentEvolutionHourly(
        @Param("empresaId") UUID empresaId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    @Query(value = """
        SELECT 
            cdd.day as timestamp,
            cdd.fk_setor as entity_id,
            s.nome as name,
            cdd.total_potencia,
            cdd.avg_potencia,
            cdd.max_potencia,
            cdd.min_potencia
        FROM consumo_daily_department cdd
        JOIN setor s ON cdd.fk_setor = s.id
        WHERE cdd.fk_empresa = :empresaId
            AND cdd.day >= :startDate
            AND cdd.day < :endDate
        ORDER BY cdd.day, s.nome
        """, nativeQuery = true)
    List<Object[]> findDepartmentEvolutionDaily(
        @Param("empresaId") UUID empresaId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    // ========================================
    // EVOLUTION - ROOM (SALA) - COM FILTRO DE SETOR
    // ========================================
    
    @Query(value = """
        SELECT 
            chr.hour as timestamp,
            chr.fk_sala as entity_id,
            sl.nome as name,
            chr.total_potencia,
            chr.avg_potencia,
            chr.max_potencia,
            chr.min_potencia
        FROM consumo_hourly_room chr
        JOIN sala sl ON chr.fk_sala = sl.id
        WHERE chr.fk_empresa = :empresaId
            AND chr.hour >= :startDate
            AND chr.hour < :endDate
            AND (:setorId IS NULL OR sl.fk_setor = :setorId)
        ORDER BY chr.hour, sl.nome
        """, nativeQuery = true)
    List<Object[]> findRoomEvolutionHourly(
        @Param("empresaId") UUID empresaId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        @Param("setorId") UUID setorId
    );
    
    @Query(value = """
        SELECT 
            cdr.day as timestamp,
            cdr.fk_sala as entity_id,
            sl.nome as name,
            cdr.total_potencia,
            cdr.avg_potencia,
            cdr.max_potencia,
            cdr.min_potencia
        FROM consumo_daily_room cdr
        JOIN sala sl ON cdr.fk_sala = sl.id
        WHERE cdr.fk_empresa = :empresaId
            AND cdr.day >= :startDate
            AND cdr.day < :endDate
            AND (:setorId IS NULL OR sl.fk_setor = :setorId)
        ORDER BY cdr.day, sl.nome
        """, nativeQuery = true)
    List<Object[]> findRoomEvolutionDaily(
        @Param("empresaId") UUID empresaId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        @Param("setorId") UUID setorId
    );

    // ========================================
    // EVOLUTION - DEVICE_ROOM (VÍNCULO) - COM FILTRO DE SALA
    // ========================================
    
    @Query(value = """
        SELECT 
            chdr.hour as timestamp,
            chdr.fk_dispositivo_sala as entity_id,
            CONCAT(d.nome, ' - ', sl.nome) as name,
            chdr.total_potencia,
            chdr.avg_potencia,
            chdr.max_potencia,
            chdr.min_potencia
        FROM consumo_hourly_device_room chdr
        JOIN dispositivo_sala ds ON chdr.fk_dispositivo_sala = ds.id
        JOIN dispositivo d ON ds.fk_dispositivo = d.id
        JOIN sala sl ON ds.fk_sala = sl.id
        WHERE chdr.fk_empresa = :empresaId
            AND chdr.hour >= :startDate
            AND chdr.hour < :endDate
            AND (:salaId IS NULL OR ds.fk_sala = :salaId)
        ORDER BY chdr.hour, d.nome, sl.nome
        """, nativeQuery = true)
    List<Object[]> findDeviceRoomEvolutionHourly(
        @Param("empresaId") UUID empresaId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        @Param("salaId") UUID salaId
    );
    
    @Query(value = """
        SELECT 
            cddr.day as timestamp,
            cddr.fk_dispositivo_sala as entity_id,
            CONCAT(d.nome, ' - ', sl.nome) as name,
            cddr.total_potencia,
            cddr.avg_potencia,
            cddr.max_potencia,
            cddr.min_potencia
        FROM consumo_daily_device_room cddr
        JOIN dispositivo_sala ds ON cddr.fk_dispositivo_sala = ds.id
        JOIN dispositivo d ON ds.fk_dispositivo = d.id
        JOIN sala sl ON ds.fk_sala = sl.id
        WHERE cddr.fk_empresa = :empresaId
            AND cddr.day >= :startDate
            AND cddr.day < :endDate
            AND (:salaId IS NULL OR ds.fk_sala = :salaId)
        ORDER BY cddr.day, d.nome, sl.nome
        """, nativeQuery = true)
    List<Object[]> findDeviceRoomEvolutionDaily(
        @Param("empresaId") UUID empresaId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        @Param("salaId") UUID salaId
    );

    // ========================================
    // RATIO - DEPARTMENT (SETOR)
    // ========================================
    
    @Query(value = """
        SELECT 
            cdd.fk_setor as entity_id,
            s.nome as name,
            SUM(cdd.total_potencia) as total_potencia
        FROM consumo_daily_department cdd
        JOIN setor s ON cdd.fk_setor = s.id
        WHERE cdd.fk_empresa = :empresaId
            AND cdd.day >= :startDate
            AND cdd.day < :endDate
        GROUP BY cdd.fk_setor, s.nome
        ORDER BY total_potencia DESC
        """, nativeQuery = true)
    List<Object[]> findDepartmentRatio(
        @Param("empresaId") UUID empresaId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    // ========================================
    // RATIO - ROOM (SALA) - COM FILTRO DE SETOR
    // ========================================
    
    @Query(value = """
        SELECT 
            cdr.fk_sala as entity_id,
            sl.nome as name,
            SUM(cdr.total_potencia) as total_potencia
        FROM consumo_daily_room cdr
        JOIN sala sl ON cdr.fk_sala = sl.id
        WHERE cdr.fk_empresa = :empresaId
            AND cdr.day >= :startDate
            AND cdr.day < :endDate
            AND (:setorId IS NULL OR sl.fk_setor = :setorId)
        GROUP BY cdr.fk_sala, sl.nome
        ORDER BY total_potencia DESC
        """, nativeQuery = true)
    List<Object[]> findRoomRatio(
        @Param("empresaId") UUID empresaId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        @Param("setorId") UUID setorId
    );

    // ========================================
    // RATIO - DEVICE_ROOM (VÍNCULO) - COM FILTRO DE SALA
    // ========================================
    
    @Query(value = """
        SELECT 
            cddr.fk_dispositivo_sala as entity_id,
            CONCAT(d.nome, ' - ', sl.nome) as name,
            SUM(cddr.total_potencia) as total_potencia
        FROM consumo_daily_device_room cddr
        JOIN dispositivo_sala ds ON cddr.fk_dispositivo_sala = ds.id
        JOIN dispositivo d ON ds.fk_dispositivo = d.id
        JOIN sala sl ON ds.fk_sala = sl.id
        WHERE cddr.fk_empresa = :empresaId
            AND cddr.day >= :startDate
            AND cddr.day < :endDate
            AND (:salaId IS NULL OR ds.fk_sala = :salaId)
        GROUP BY cddr.fk_dispositivo_sala, d.nome, sl.nome
        ORDER BY total_potencia DESC
        """, nativeQuery = true)
    List<Object[]> findDeviceRoomRatio(
        @Param("empresaId") UUID empresaId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        @Param("salaId") UUID salaId
    );
}