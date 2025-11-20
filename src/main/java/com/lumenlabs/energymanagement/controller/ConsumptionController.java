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
import com.lumenlabs.energymanagement.dto.consumption.DeviceConsumptionDetailResponse;
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
			@RequestParam(required = false) UUID departmentId,
			
			@Parameter(description = "ID da sala (opcional, apenas para DEVICE_ROOM)")
			@RequestParam(required = false) UUID roomId) {
		
		ConsumptionEvolutionResponse response = consumptionService.getEvolution(
			securityUtils.getLoggedUserCompany().getId(), 
			resource, 
			start, 
			end,
			departmentId,
			roomId
		);
		
		return ResponseEntity.ok(response);
	}

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
			@RequestParam(required = false) UUID departmentId,
			
			@Parameter(description = "ID da sala (opcional, apenas para DEVICE_ROOM)")
			@RequestParam(required = false) UUID roomId) {
		
		ConsumptionRatioResponse response = consumptionService.getRatio(
			securityUtils.getLoggedUserCompany().getId(), 
			resource, 
			start, 
			end,
			departmentId,
			roomId
		);
		
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/device/detail")
	@Operation(
	    summary = "Obter detalhes de consumo de um dispositivo",
	    description = "Retorna os dados de Tensão, Corrente e Potência Ativa de um dispositivo específico"
	)
	public ResponseEntity<DeviceConsumptionDetailResponse> getDeviceDetail(
	        @Parameter(description = "ID do vínculo dispositivo-sala", required = true)
	        @RequestParam UUID deviceRoomId,
	        
	        @Parameter(description = "Data de início", required = true, example = "2024-01-01T00:00:00")
	        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
	        
	        @Parameter(description = "Data de fim", required = true, example = "2024-01-31T23:59:59")
	        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
	    
	    DeviceConsumptionDetailResponse response = consumptionService.getDeviceDetail(
	        securityUtils.getLoggedUserCompany().getId(),
	        deviceRoomId,
	        start,
	        end
	    );
	    
	    return ResponseEntity.ok(response);
	}
}