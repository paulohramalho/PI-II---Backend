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
	List<Object[]> findDepartmentEvolutionHourly(@Param("empresaId") UUID empresaId,
			@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

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
	List<Object[]> findDepartmentEvolutionDaily(@Param("empresaId") UUID empresaId,
			@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

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
	List<Object[]> findRoomEvolutionHourly(@Param("empresaId") UUID empresaId,
			@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate,
			@Param("setorId") UUID setorId);

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
	List<Object[]> findRoomEvolutionDaily(@Param("empresaId") UUID empresaId,
			@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate,
			@Param("setorId") UUID setorId);

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
	List<Object[]> findDeviceRoomEvolutionHourly(@Param("empresaId") UUID empresaId,
			@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate,
			@Param("salaId") UUID salaId);

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
	List<Object[]> findDeviceRoomEvolutionDaily(@Param("empresaId") UUID empresaId,
			@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate,
			@Param("salaId") UUID salaId);

	// ========================================
	// RATIO - DEPARTMENT (SETOR)
	// ========================================

	@Query(value = """
	        SELECT
	            chd.fk_setor as entity_id,
	            s.nome as name,
	            SUM(chd.total_potencia) as total_potencia
	        FROM consumo_hourly_department chd
	        JOIN setor s ON chd.fk_setor = s.id
	        WHERE chd.fk_empresa = :empresaId
	            AND chd.hour >= :startDate
	            AND chd.hour < :endDate
	        GROUP BY chd.fk_setor, s.nome
	        ORDER BY total_potencia DESC
	        """, nativeQuery = true)
	List<Object[]> findDepartmentRatio(@Param("empresaId") UUID empresaId,
	                                   @Param("startDate") LocalDateTime startDate,
	                                   @Param("endDate") LocalDateTime endDate);


	// ========================================
	// RATIO - ROOM (SALA) - COM FILTRO DE SETOR
	// ========================================

	@Query(value = """
	        SELECT
	            chr.fk_sala as entity_id,
	            sl.nome as name,
	            SUM(chr.total_potencia) as total_potencia
	        FROM consumo_hourly_room chr
	        JOIN sala sl ON chr.fk_sala = sl.id
	        WHERE chr.fk_empresa = :empresaId
	            AND chr.hour >= :startDate
	            AND chr.hour < :endDate
	            AND (:setorId IS NULL OR sl.fk_setor = :setorId)
	        GROUP BY chr.fk_sala, sl.nome
	        ORDER BY total_potencia DESC
	        """, nativeQuery = true)
	List<Object[]> findRoomRatio(@Param("empresaId") UUID empresaId,
	                             @Param("startDate") LocalDateTime startDate,
	                             @Param("endDate") LocalDateTime endDate,
	                             @Param("setorId") UUID setorId);


	// ========================================
	// RATIO - DEVICE_ROOM (VÍNCULO) - COM FILTRO DE SALA
	// ========================================

	@Query(value = """
	        SELECT
	            chdr.fk_dispositivo_sala as entity_id,
	            CONCAT(d.nome, ' - ', sl.nome) as name,
	            SUM(chdr.total_potencia) as total_potencia
	        FROM consumo_hourly_device_room chdr
	        JOIN dispositivo_sala ds ON chdr.fk_dispositivo_sala = ds.id
	        JOIN dispositivo d ON ds.fk_dispositivo = d.id
	        JOIN sala sl ON ds.fk_sala = sl.id
	        WHERE chdr.fk_empresa = :empresaId
	            AND chdr.hour >= :startDate
	            AND chdr.hour < :endDate
	            AND (:salaId IS NULL OR ds.fk_sala = :salaId)
	        GROUP BY chdr.fk_dispositivo_sala, d.nome, sl.nome
	        ORDER BY total_potencia DESC
	        """, nativeQuery = true)
	List<Object[]> findDeviceRoomRatio(@Param("empresaId") UUID empresaId,
	                                   @Param("startDate") LocalDateTime startDate,
	                                   @Param("endDate") LocalDateTime endDate,
	                                   @Param("salaId") UUID salaId);

	// ========================================
	// DEVICE DETAIL - DADOS HORÁRIOS
	// ========================================

	@Query(value = """
			SELECT
			    chdr.hour as timestamp,
			    chdr.avg_tensao as avg_tensao,
			    chdr.max_tensao as max_tensao,
			    chdr.min_tensao as min_tensao,
			    chdr.avg_corrente as avg_corrente,
			    chdr.max_corrente as max_corrente,
			    chdr.min_corrente as min_corrente,
			    chdr.avg_potencia as avg_potencia,
			    chdr.max_potencia as max_potencia,
			    chdr.min_potencia as min_potencia,
			    chdr.total_leituras
			FROM consumo_hourly_device_room chdr
			WHERE chdr.fk_empresa = :empresaId
			    AND chdr.fk_dispositivo_sala = :deviceRoomId
			    AND chdr.hour >= :startDate
			    AND chdr.hour < :endDate
			ORDER BY chdr.hour
			""", nativeQuery = true)
	List<Object[]> findDeviceDetailHourly(@Param("empresaId") UUID empresaId, @Param("deviceRoomId") UUID deviceRoomId,
			@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

	// ========================================
	// DEVICE DETAIL - DADOS DIÁRIOS
	// ========================================

	@Query(value = """
			SELECT
			    cddr.day as timestamp,
			    cddr.avg_tensao as avg_tensao,
			    cddr.max_tensao as max_tensao,
			    cddr.min_tensao as min_tensao,
			    cddr.avg_corrente as avg_corrente,
			    cddr.max_corrente as max_corrente,
			    cddr.min_corrente as min_corrente,
			    cddr.avg_potencia as avg_potencia,
			    cddr.max_potencia as max_potencia,
			    cddr.min_potencia as min_potencia,
			    cddr.total_leituras
			FROM consumo_daily_device_room cddr
			WHERE cddr.fk_empresa = :empresaId
			    AND cddr.fk_dispositivo_sala = :deviceRoomId
			    AND cddr.day >= :startDate
			    AND cddr.day < :endDate
			ORDER BY cddr.day
			""", nativeQuery = true)
	List<Object[]> findDeviceDetailDaily(@Param("empresaId") UUID empresaId, @Param("deviceRoomId") UUID deviceRoomId,
			@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
	
	@Query(value = """
            SELECT SUM(chdr.total_potencia)/1000 as total_consumo
            FROM consumo_hourly_device_room chdr
            WHERE chdr.fk_empresa = :empresaId
                AND chdr.hour >= :startDate
                AND chdr.hour < :endDate
            """, nativeQuery = true)
    Double getTotalConsumptionByCompany(
            @Param("empresaId") UUID empresaId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // ========================================
    // DASHBOARD - CONSUMO POR HORA (EMPRESA INTEIRA)
    // ========================================
    
    @Query(value = """
            SELECT
                chdr.hour as timestamp,
                SUM(chdr.total_potencia)/1000 as total_potencia,
                AVG(chdr.avg_potencia) as avg_potencia,
                MAX(chdr.max_potencia) as max_potencia,
                MIN(chdr.min_potencia) as min_potencia
            FROM consumo_hourly_device_room chdr
            WHERE chdr.fk_empresa = :empresaId
                AND chdr.hour >= :startDate
                AND chdr.hour < :endDate
            GROUP BY chdr.hour
            ORDER BY chdr.hour
            """, nativeQuery = true)
    List<Object[]> getHourlyConsumptionByCompany(
            @Param("empresaId") UUID empresaId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // ========================================
    // DASHBOARD - TOP N DISPOSITIVOS
    // ========================================
    
    @Query(value = """
            SELECT
                CONCAT(d.nome, ' - ', sl.nome) as device_name,
                SUM(chdr.total_potencia)/1000 as total_potencia
            FROM consumo_hourly_device_room chdr
            JOIN dispositivo_sala ds ON chdr.fk_dispositivo_sala = ds.id
            JOIN dispositivo d ON ds.fk_dispositivo = d.id
            JOIN sala sl ON ds.fk_sala = sl.id
            WHERE chdr.fk_empresa = :empresaId
                AND chdr.hour >= :startDate
                AND chdr.hour < :endDate
            GROUP BY d.nome, sl.nome
            ORDER BY total_potencia DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<Object[]> getTopDevicesByConsumption(
            @Param("empresaId") UUID empresaId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("limit") int limit);

    // ========================================
    // DASHBOARD - CONSUMO POR SETOR
    // ========================================
    
    @Query(value = """
            SELECT
                s.nome as setor_name,
                SUM(chd.total_potencia)/1000 as total_potencia
            FROM consumo_hourly_department chd
            JOIN setor s ON chd.fk_setor = s.id
            WHERE chd.fk_empresa = :empresaId
                AND chd.hour >= :startDate
                AND chd.hour < :endDate
            GROUP BY s.nome
            ORDER BY total_potencia DESC
            """, nativeQuery = true)
    List<Object[]> getConsumptionByDepartment(
            @Param("empresaId") UUID empresaId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // ========================================
    // DASHBOARD - ÚLTIMAS LEITURAS
    // ========================================
    
    @Query(value = """
            SELECT
                c.event_time,
                CONCAT(ds.apelido, ' - ', sl.nome) as device_name,
                c.corrente,
                c.tensao,
                c.potencia_ativa
            FROM consumo c
            JOIN dispositivo_sala ds ON c.fk_dispositivo_sala = ds.id
            JOIN dispositivo d ON ds.fk_dispositivo = d.id
            JOIN sala sl ON ds.fk_sala = sl.id
            WHERE ds.fk_empresa = :empresaId
            ORDER BY c.event_time DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<Object[]> getLatestReadings(
            @Param("empresaId") UUID empresaId,
            @Param("limit") int limit);
}