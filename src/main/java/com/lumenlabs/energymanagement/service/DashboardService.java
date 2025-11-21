package com.lumenlabs.energymanagement.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lumenlabs.energymanagement.dto.dashboard.ConsumoPorHoraDTO;
import com.lumenlabs.energymanagement.dto.dashboard.ConsumoPorSetorDTO;
import com.lumenlabs.energymanagement.dto.dashboard.DashboardResponse;
import com.lumenlabs.energymanagement.dto.dashboard.EstatisticasDTO;
import com.lumenlabs.energymanagement.dto.dashboard.TopDispositivoDTO;
import com.lumenlabs.energymanagement.dto.dashboard.UltimaLeituraDTO;
import com.lumenlabs.energymanagement.repository.ConsumptionRepository;
import com.lumenlabs.energymanagement.repository.DeviceRoomRepository;

@Service
public class DashboardService {

	@Autowired
	private ConsumptionRepository consumptionRepository;

	@Autowired
	private DeviceRoomRepository deviceRoomRepository;

	public DashboardResponse getDashboardData(UUID empresaId, LocalDateTime startDate, LocalDateTime endDate) {

		// 1. ESTATÍSTICAS
		EstatisticasDTO estatisticas = getEstatisticas(empresaId, startDate, endDate);

		// 2. CONSUMO POR HORA
		List<ConsumoPorHoraDTO> consumoPorHora = getConsumoPorHora(empresaId, startDate, endDate);

		// 3. TOP 5 DISPOSITIVOS
		List<TopDispositivoDTO> topDispositivos = getTopDispositivos(empresaId, startDate, endDate);

		// 4. CONSUMO POR SETOR
		List<ConsumoPorSetorDTO> consumoPorSetor = getConsumoPorSetor(empresaId, startDate, endDate);

		// 5. ÚLTIMAS LEITURAS
		List<UltimaLeituraDTO> ultimasLeituras = getUltimasLeituras(empresaId);

		DashboardResponse response = new DashboardResponse();
		response.setEstatisticas(estatisticas);
		response.setConsumoPorHora(consumoPorHora);
		response.setTopDispositivos(topDispositivos);
		response.setConsumoPorSetor(consumoPorSetor);
		response.setUltimasLeituras(ultimasLeituras);
		return response;

	}

	// ========================================
	// ESTATÍSTICAS
	// ========================================

	private EstatisticasDTO getEstatisticas(UUID empresaId, LocalDateTime startDate, LocalDateTime endDate) {

		// Consumo hoje (período selecionado)
		Double consumoHoje = consumptionRepository.getTotalConsumptionByCompany(empresaId, startDate, endDate);
		if (consumoHoje == null)
			consumoHoje = 0.0;

		// Consumo ontem (mesmo período, mas 1 dia antes)
		LocalDateTime yesterdayStart = startDate.minusDays(1);
		LocalDateTime yesterdayEnd = endDate.minusDays(1);
		Double consumoOntem = consumptionRepository.getTotalConsumptionByCompany(empresaId, yesterdayStart,
				yesterdayEnd);
		if (consumoOntem == null)
			consumoOntem = 0.0;

		// Variação percentual
		Double variacao = 0.0;
		if (consumoOntem > 0) {
			variacao = ((consumoHoje - consumoOntem) / consumoOntem) * 100;
		}

		// Consumo mensal (do dia 1 até hoje)
		LocalDateTime mesStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
		LocalDateTime mesEnd = LocalDateTime.now();
		Double consumoMensal = consumptionRepository.getTotalConsumptionByCompany(empresaId, mesStart, mesEnd);
		if (consumoMensal == null)
			consumoMensal = 0.0;

		// Dispositivos ativos (count de dispositivo_sala)
		Long dispositivosAtivos = deviceRoomRepository.countByCompanyId(empresaId);
		if (dispositivosAtivos == null)
			dispositivosAtivos = 0L;

		// Custo estimado (R$ 0.65 por kWh - ajuste conforme sua tarifa)
		Double tarifaKwh = 0.65;
		Double custoEstimado = consumoMensal * tarifaKwh;

		EstatisticasDTO dto = new EstatisticasDTO();
		dto.setConsumoHoje(consumoHoje);
		dto.setConsumoHojeVariacao(variacao);
		dto.setConsumoMensal(consumoMensal);
		dto.setDispositivosAtivos(dispositivosAtivos.intValue());
		dto.setCustoEstimado(custoEstimado);
		return dto;

	}

	// ========================================
	// CONSUMO POR HORA
	// ========================================

	private List<ConsumoPorHoraDTO> getConsumoPorHora(UUID empresaId, LocalDateTime startDate, LocalDateTime endDate) {

		List<Object[]> rawData = consumptionRepository.getHourlyConsumptionByCompany(empresaId, startDate, endDate);

		if (rawData == null || rawData.isEmpty()) {
			return new ArrayList<>();
		}

		return rawData.stream().map(row -> {
			LocalDateTime timestamp = extractTimestamp(row[0]);
			Double consumo = safeGetDouble(row[1], 0.0);
			Double media = safeGetDouble(row[2], 0.0);
			Double maximo = safeGetDouble(row[3], 0.0);
			Double minimo = safeGetDouble(row[4], 0.0);

			String hora = String.format("%02d:00", timestamp.getHour());

			ConsumoPorHoraDTO dto = new ConsumoPorHoraDTO();
			dto.setHora(hora);
			dto.setConsumo(consumo);
			dto.setMedia(media);
			dto.setMaximo(maximo);
			dto.setMinimo(minimo);
			return dto;
		}).collect(Collectors.toList());
	}

	// ========================================
	// TOP 5 DISPOSITIVOS
	// ========================================

	private List<TopDispositivoDTO> getTopDispositivos(UUID empresaId, LocalDateTime startDate, LocalDateTime endDate) {

		List<Object[]> rawData = consumptionRepository.getTopDevicesByConsumption(empresaId, startDate, endDate, 5);

		if (rawData == null || rawData.isEmpty()) {
			return new ArrayList<>();
		}

		return rawData.stream().map(row -> {
			String nome = safeGetString(row[0], "Dispositivo Desconhecido");
			Double consumo = safeGetDouble(row[1], 0.0);

			TopDispositivoDTO dto = new TopDispositivoDTO();
			dto.setNome(nome);
			dto.setConsumo(consumo);
			return dto;
		}).collect(Collectors.toList());
	}

	// ========================================
	// CONSUMO POR SETOR
	// ========================================

	private List<ConsumoPorSetorDTO> getConsumoPorSetor(UUID empresaId, LocalDateTime startDate,
			LocalDateTime endDate) {

		List<Object[]> rawData = consumptionRepository.getConsumptionByDepartment(empresaId, startDate, endDate);

		if (rawData == null || rawData.isEmpty()) {
			return new ArrayList<>();
		}

		return rawData.stream().map(row -> {
			String setor = safeGetString(row[0], "Setor Desconhecido");
			Double consumo = safeGetDouble(row[1], 0.0);

			ConsumoPorSetorDTO dto = new ConsumoPorSetorDTO();
			dto.setSetor(setor);
			dto.setConsumo(consumo);
			return dto;
		}).collect(Collectors.toList());
	}

	// ========================================
	// ÚLTIMAS LEITURAS
	// ========================================

	private List<UltimaLeituraDTO> getUltimasLeituras(UUID empresaId) {

		List<Object[]> rawData = consumptionRepository.getLatestReadings(empresaId, 10);

		if (rawData == null || rawData.isEmpty()) {
			return new ArrayList<>();
		}

		return rawData.stream().map(row -> {
			LocalDateTime timestamp = extractTimestamp(row[0]);
			String dispositivo = safeGetString(row[1], "Dispositivo Desconhecido");
			Double corrente = safeGetDouble(row[2], 0.0);
			Double tensao = safeGetDouble(row[3], 0.0);
			Double potenciaAtiva = safeGetDouble(row[4], 0.0);

			String data = String.format("%02d/%02d/%d", timestamp.getDayOfMonth(), timestamp.getMonthValue(),
					timestamp.getYear());

			String hora = String.format("%02d:%02d:%02d", timestamp.getHour(), timestamp.getMinute(),
					timestamp.getSecond());

			UltimaLeituraDTO dto = new UltimaLeituraDTO();
			dto.setData(data);
			dto.setHora(hora);
			dto.setDispositivo(dispositivo);
			dto.setCorrente(corrente);
			dto.setTensao(tensao);
			dto.setPotenciaAtiva(potenciaAtiva);
			return dto;
		}).collect(Collectors.toList());
	}

	// ========================================
	// MÉTODOS AUXILIARES
	// ========================================

	private LocalDateTime extractTimestamp(Object value) {
		if (value == null)
			return null;

		try {
			if (value instanceof java.sql.Timestamp) {
				return ((java.sql.Timestamp) value).toLocalDateTime();
			}
			if (value instanceof LocalDateTime) {
				return (LocalDateTime) value;
			}
			if (value instanceof java.time.Instant) {
				return LocalDateTime.ofInstant((java.time.Instant) value, java.time.ZoneId.of("America/Sao_Paulo"));
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	private Double safeGetDouble(Object value, Double defaultValue) {
		if (value == null)
			return defaultValue;

		try {
			if (value instanceof Number) {
				return ((Number) value).doubleValue();
			}
			return Double.parseDouble(value.toString());
		} catch (Exception e) {
			return defaultValue;
		}
	}

	private String safeGetString(Object value, String defaultValue) {
		if (value == null)
			return defaultValue;
		return value.toString();
	}
}