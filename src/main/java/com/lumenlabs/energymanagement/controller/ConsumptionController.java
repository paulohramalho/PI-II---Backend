package com.lumenlabs.energymanagement.controller;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lumenlabs.energymanagement.dto.consumption.ConsumptionEvolutionResponse;
import com.lumenlabs.energymanagement.dto.consumption.ConsumptionRatioResponse;
import com.lumenlabs.energymanagement.enums.ResourceType;
import com.lumenlabs.energymanagement.service.ConsumptionService;
import com.lumenlabs.energymanagement.util.SecurityUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/consumption")
@Tag(name = "Consumo")
public class ConsumptionController {

	@Autowired
	private ConsumptionService consumptionService;

	@Autowired
	private SecurityUtils securityUtils;

	/**
	 * Endpoint de evolução de consumo com filtros opcionais
	 * 
	 * Exemplos:
	 * - DEPARTMENT: /api/consumption/evolution?resource=DEPARTMENT&start=2024-01-01T00:00:00&end=2024-01-31T23:59:59
	 * - ROOM (todas): /api/consumption/evolution?resource=ROOM&start=2024-01-01T00:00:00&end=2024-01-31T23:59:59
	 * - ROOM (filtrado por setor): /api/consumption/evolution?resource=ROOM&start=2024-01-01T00:00:00&end=2024-01-31T23:59:59&setorId=a0000000-0000-0000-0000-000000000101
	 * - DEVICE_ROOM (todos): /api/consumption/evolution?resource=DEVICE_ROOM&start=2024-01-01T00:00:00&end=2024-01-31T23:59:59
	 * - DEVICE_ROOM (filtrado por sala): /api/consumption/evolution?resource=DEVICE_ROOM&start=2024-01-01T00:00:00&end=2024-01-31T23:59:59&salaId=a0000000-0000-0000-0000-000000000201
	 */
	@GetMapping("/evolution")
	@Operation(
		summary = "Obter evolução do consumo",
		description = "Retorna a evolução temporal do consumo. Filtros opcionais: setorId (para ROOM), salaId (para DEVICE_ROOM)"
	)
	public ResponseEntity<ConsumptionEvolutionResponse> getEvolution(
			@Parameter(description = "Tipo de recurso", required = true)
			@RequestParam ResourceType resource,
			
			@Parameter(description = "Data de início", required = true, example = "2024-01-01T00:00:00")
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
			
			@Parameter(description = "Data de fim", required = true, example = "2024-01-31T23:59:59")
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
			
			@Parameter(description = "ID do setor (opcional, apenas para ROOM)")
			@RequestParam(required = false) UUID setorId,
			
			@Parameter(description = "ID da sala (opcional, apenas para DEVICE_ROOM)")
			@RequestParam(required = false) UUID salaId) {
		
		ConsumptionEvolutionResponse response = consumptionService.getEvolution(
			securityUtils.getLoggedUserCompany().getId(), 
			resource, 
			start, 
			end,
			setorId,
			salaId
		);
		
		return ResponseEntity.ok(response);
	}

	/**
	 * Endpoint de proporção de consumo com filtros opcionais
	 * 
	 * Exemplos:
	 * - DEPARTMENT: /api/consumption/ratio?resource=DEPARTMENT&start=2024-01-01T00:00:00&end=2024-01-31T23:59:59
	 * - ROOM (todas): /api/consumption/ratio?resource=ROOM&start=2024-01-01T00:00:00&end=2024-01-31T23:59:59
	 * - ROOM (filtrado por setor): /api/consumption/ratio?resource=ROOM&start=2024-01-01T00:00:00&end=2024-01-31T23:59:59&setorId=a0000000-0000-0000-0000-000000000101
	 * - DEVICE_ROOM (todos): /api/consumption/ratio?resource=DEVICE_ROOM&start=2024-01-01T00:00:00&end=2024-01-31T23:59:59
	 * - DEVICE_ROOM (filtrado por sala): /api/consumption/ratio?resource=DEVICE_ROOM&start=2024-01-01T00:00:00&end=2024-01-31T23:59:59&salaId=a0000000-0000-0000-0000-000000000201
	 */
	@GetMapping("/ratio")
	@Operation(
		summary = "Obter proporção do consumo",
		description = "Retorna a proporção de consumo entre entidades. Filtros opcionais: setorId (para ROOM), salaId (para DEVICE_ROOM)"
	)
	public ResponseEntity<ConsumptionRatioResponse> getRatio(
			@Parameter(description = "Tipo de recurso", required = true)
			@RequestParam ResourceType resource,
			
			@Parameter(description = "Data de início", required = true, example = "2024-01-01T00:00:00")
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
			
			@Parameter(description = "Data de fim", required = true, example = "2024-01-31T23:59:59")
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
			
			@Parameter(description = "ID do setor (opcional, apenas para ROOM)")
			@RequestParam(required = false) UUID setorId,
			
			@Parameter(description = "ID da sala (opcional, apenas para DEVICE_ROOM)")
			@RequestParam(required = false) UUID salaId) {
		
		ConsumptionRatioResponse response = consumptionService.getRatio(
			securityUtils.getLoggedUserCompany().getId(), 
			resource, 
			start, 
			end,
			setorId,
			salaId
		);
		
		return ResponseEntity.ok(response);
	}
}