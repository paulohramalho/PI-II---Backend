package com.lumenlabs.energymanagement.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.lumenlabs.energymanagement.dto.consumption.ConsumptionEvolutionResponse;
import com.lumenlabs.energymanagement.dto.consumption.ConsumptionRatioResponse;
import com.lumenlabs.energymanagement.dto.consumption.DataPoint;
import com.lumenlabs.energymanagement.dto.consumption.DeviceConsumptionDetailResponse;
import com.lumenlabs.energymanagement.dto.consumption.RatioData;
import com.lumenlabs.energymanagement.dto.consumption.SeriesData;
import com.lumenlabs.energymanagement.dto.consumption.Statistics;
import com.lumenlabs.energymanagement.enums.Granularity;
import com.lumenlabs.energymanagement.enums.ResourceType;
import com.lumenlabs.energymanagement.model.DeviceRoom;
import com.lumenlabs.energymanagement.repository.ConsumptionRepository;
import com.lumenlabs.energymanagement.repository.DeviceRoomRepository;

@Service
public class ConsumptionService {

	@Autowired
	private ConsumptionRepository consumptionRepository;
	
	@Autowired
	private DeviceRoomRepository deviceRoomRepository;

	public ConsumptionEvolutionResponse getEvolution(UUID empresaId, ResourceType resourceType, LocalDateTime startDate,
			LocalDateTime endDate, UUID setorId, UUID salaId) {
		Granularity granularity = determineGranularity(startDate, endDate);

		List<Object[]> rawData = fetchEvolutionData(empresaId, resourceType, granularity, startDate, endDate, setorId, salaId);

		List<SeriesData> series = processEvolutionData(rawData, granularity);

		return ConsumptionEvolutionResponse.builder()
				.resourceType(resourceType.name())
				.granularity(granularity.name())
				.startDate(startDate)
				.endDate(endDate)
				.series(series)
				.build();
	}

	private Granularity determineGranularity(LocalDateTime start, LocalDateTime end) {
	    long hoursBetween = ChronoUnit.HOURS.between(start, end);
	    long daysBetween = ChronoUnit.DAYS.between(start, end);

	    if (hoursBetween <= 48) {
	        return Granularity.HOURLY;
	    } else if (daysBetween <= 31) {
	        return Granularity.DAILY;
	    } else if (daysBetween <= 90) {
	        return Granularity.WEEKLY;
	    } else {
	        return Granularity.MONTHLY;
	    }
	}

	private List<Object[]> fetchEvolutionData(UUID empresaId, ResourceType resourceType, Granularity granularity,
			LocalDateTime startDate, LocalDateTime endDate, UUID setorId, UUID salaId) {
		boolean useHourly = granularity == Granularity.HOURLY;

		switch (resourceType) {
		case DEPARTMENT:
			return useHourly 
				? consumptionRepository.findDepartmentEvolutionHourly(empresaId, startDate, endDate)
				: consumptionRepository.findDepartmentEvolutionDaily(empresaId, startDate, endDate);
		case ROOM:
			return useHourly 
				? consumptionRepository.findRoomEvolutionHourly(empresaId, startDate, endDate, setorId)
				: consumptionRepository.findRoomEvolutionDaily(empresaId, startDate, endDate, setorId);
		case DEVICE_ROOM:
			return useHourly 
				? consumptionRepository.findDeviceRoomEvolutionHourly(empresaId, startDate, endDate, salaId)
				: consumptionRepository.findDeviceRoomEvolutionDaily(empresaId, startDate, endDate, salaId);
		default:
			return new ArrayList<>();
		}
	}

	private List<SeriesData> processEvolutionData(List<Object[]> rawData, Granularity granularity) {
		if (rawData == null || rawData.isEmpty()) {
			return new ArrayList<>();
		}

		Map<UUID, List<Object[]>> groupedByEntity = new HashMap<>();

		for (Object[] row : rawData) {
			try {
				if (row[1] == null)
					continue;

				UUID entityId = parseUUID(row[1]);
				if (entityId == null)
					continue;

				groupedByEntity.computeIfAbsent(entityId, k -> new ArrayList<>()).add(row);
			} catch (Exception e) {
				continue;
			}
		}

		List<SeriesData> series = new ArrayList<>();

		for (Map.Entry<UUID, List<Object[]>> entry : groupedByEntity.entrySet()) {
			UUID entityId = entry.getKey();
			List<Object[]> entityData = entry.getValue();

			if (entityData.isEmpty())
				continue;

			String entityName = safeGetString(entityData.get(0)[2], "Unknown");

			List<DataPoint> points = (granularity == Granularity.WEEKLY || granularity == Granularity.MONTHLY)
					? aggregateDataPoints(entityData, granularity)
					: convertToDataPoints(entityData);

			if (points.isEmpty())
				continue;

			Statistics stats = calculateStatistics(points);

			SeriesData seriesData = SeriesData.builder()
					.name(entityName)
					.entityId(entityId)
					.points(points)
					.statistics(stats)
					.build();

			series.add(seriesData);
		}

		return series;
	}

	private List<DataPoint> convertToDataPoints(List<Object[]> data) {
		List<DataPoint> dataPoints = new ArrayList<>();

		for (Object[] row : data) {
			try {
				LocalDateTime timestamp = extractTimestamp(row[0]);
				if (timestamp == null)
					continue;

				Double totalPotencia = safeGetDouble(row[3], 0.0);
				Double avgPotencia = safeGetDouble(row[4], 0.0);
				Double maxPotencia = safeGetDouble(row[5], 0.0);
				Double minPotencia = safeGetDouble(row[6], 0.0);

				DataPoint point = DataPoint.builder()
						.timestamp(timestamp)
						.totalPotencia(totalPotencia)
						.avgPotencia(avgPotencia)
						.maxPotencia(maxPotencia)
						.minPotencia(minPotencia)
						.build();

				dataPoints.add(point);
			} catch (Exception e) {
				continue;
			}
		}

		return dataPoints;
	}

	private List<DataPoint> aggregateDataPoints(List<Object[]> data, Granularity granularity) {
		Map<LocalDateTime, List<Object[]>> grouped = new HashMap<>();

		for (Object[] row : data) {
			try {
				LocalDateTime timestamp = extractTimestamp(row[0]);
				if (timestamp == null)
					continue;

				LocalDateTime bucketKey;

				if (granularity == Granularity.WEEKLY) {
					bucketKey = timestamp.truncatedTo(ChronoUnit.DAYS)
							.minusDays(timestamp.getDayOfWeek().getValue() - 1);
				} else {
					bucketKey = timestamp.withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
				}

				grouped.computeIfAbsent(bucketKey, k -> new ArrayList<>()).add(row);
			} catch (Exception e) {
				continue;
			}
		}

		List<LocalDateTime> sortedKeys = new ArrayList<>(grouped.keySet());
		Collections.sort(sortedKeys);

		List<DataPoint> dataPoints = new ArrayList<>();

		for (LocalDateTime key : sortedKeys) {
			List<Object[]> groupData = grouped.get(key);

			double sumTotal = 0.0;
			double sumAvg = 0.0;
			double maxValue = Double.NEGATIVE_INFINITY;
			double minValue = Double.POSITIVE_INFINITY;
			int validCount = 0;

			for (Object[] row : groupData) {
				try {
					Double total = safeGetDouble(row[3], null);
					Double avg = safeGetDouble(row[4], null);
					Double max = safeGetDouble(row[5], null);
					Double min = safeGetDouble(row[6], null);

					if (total != null) {
						sumTotal += total;
						validCount++;
					}
					if (avg != null)
						sumAvg += avg;
					if (max != null && max > maxValue)
						maxValue = max;
					if (min != null && min < minValue)
						minValue = min;
				} catch (Exception e) {
					continue;
				}
			}

			if (validCount == 0)
				continue;

			double avgTotal = sumAvg / validCount;

			if (Double.isInfinite(maxValue))
				maxValue = 0.0;
			if (Double.isInfinite(minValue))
				minValue = 0.0;

			DataPoint point = DataPoint.builder()
					.timestamp(key)
					.totalPotencia(sumTotal)
					.avgPotencia(avgTotal)
					.maxPotencia(maxValue)
					.minPotencia(minValue)
					.build();

			dataPoints.add(point);
		}

		return dataPoints;
	}

	private Statistics calculateStatistics(List<DataPoint> points) {
		if (points == null || points.isEmpty()) {
			return Statistics.builder()
					.totalConsumption(0.0)
					.avgConsumption(0.0)
					.maxConsumption(0.0)
					.minConsumption(0.0)
					.totalReadings(0L)
					.build();
		}

		double total = 0.0;
		double sumAvg = 0.0;
		double max = Double.NEGATIVE_INFINITY;
		double min = Double.POSITIVE_INFINITY;

		for (DataPoint point : points) {
			total += point.getTotalPotencia();
			sumAvg += point.getAvgPotencia();

			if (point.getMaxPotencia() > max) {
				max = point.getMaxPotencia();
			}
			if (point.getMinPotencia() < min) {
				min = point.getMinPotencia();
			}
		}

		double avg = sumAvg / points.size();

		if (Double.isInfinite(max))
			max = 0.0;
		if (Double.isInfinite(min))
			min = 0.0;

		return Statistics.builder()
				.totalConsumption(total)
				.avgConsumption(avg)
				.maxConsumption(max)
				.minConsumption(min)
				.totalReadings((long) points.size())
				.build();
	}


	public ConsumptionRatioResponse getRatio(UUID empresaId, ResourceType resourceType, LocalDateTime startDate,
			LocalDateTime endDate, UUID setorId, UUID salaId) {
		List<Object[]> rawData = fetchRatioData(empresaId, resourceType, startDate, endDate, setorId, salaId);

		if (rawData == null || rawData.isEmpty()) {
			return ConsumptionRatioResponse.builder()
					.resourceType(resourceType.name())
					.startDate(startDate)
					.endDate(endDate)
					.data(new ArrayList<>())
					.totalConsumption(0.0)
					.build();
		}

		double totalConsumption = 0.0;
		for (Object[] row : rawData) {
			Double consumption = safeGetDouble(row[2], 0.0);
			totalConsumption += consumption;
		}

		List<RatioData> ratioData = new ArrayList<>();

		for (Object[] row : rawData) {
			try {
				UUID entityId = parseUUID(row[0]);
				if (entityId == null)
					continue;

				String name = safeGetString(row[1], "Unknown");
				double consumption = safeGetDouble(row[2], 0.0);
				double percentage = totalConsumption > 0 ? (consumption / totalConsumption) * 100 : 0.0;

				RatioData ratio = RatioData.builder()
						.entityId(entityId)
						.name(name)
						.consumption(consumption)
						.percentage(percentage)
						.build();

				ratioData.add(ratio);
			} catch (Exception e) {
				continue;
			}
		}

		return ConsumptionRatioResponse.builder()
				.resourceType(resourceType.name())
				.startDate(startDate)
				.endDate(endDate)
				.data(ratioData)
				.totalConsumption(totalConsumption)
				.build();
	}

	private List<Object[]> fetchRatioData(UUID empresaId, ResourceType resourceType, LocalDateTime startDate,
			LocalDateTime endDate, UUID setorId, UUID salaId) {
		switch (resourceType) {
		case DEPARTMENT:
			return consumptionRepository.findDepartmentRatio(empresaId, startDate, endDate);
		case ROOM:
			return consumptionRepository.findRoomRatio(empresaId, startDate, endDate, setorId);
		case DEVICE_ROOM:
			return consumptionRepository.findDeviceRoomRatio(empresaId, startDate, endDate, salaId);
		default:
			return new ArrayList<>();
		}
	}

	// ========================================
	// MÉTODOS AUXILIARES
	// ========================================

	private UUID parseUUID(Object value) {
		if (value == null)
			return null;

		try {
			if (value instanceof UUID) {
				return (UUID) value;
			}
			return UUID.fromString(value.toString());
		} catch (Exception e) {
			return null;
		}
	}

	private LocalDateTime extractTimestamp(Object value) {
		if (value == null)
			return null;

		try {
			if (value instanceof Timestamp) {
				return ((Timestamp) value).toLocalDateTime();
			}
			if (value instanceof LocalDateTime) {
				return (LocalDateTime) value;
			}
			if (value instanceof Instant) {
				// TimescaleDB retorna TIMESTAMPTZ como Instant
				return LocalDateTime.ofInstant((Instant) value, ZoneId.of("UTC"));
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
	
	public DeviceConsumptionDetailResponse getDeviceDetail(
	        UUID empresaId, 
	        UUID deviceRoomId, 
	        LocalDateTime startDate,
	        LocalDateTime endDate) {
	    
	    // Determinar granularidade
	    Granularity granularity = determineGranularity(startDate, endDate);
	    
	    // Buscar informações do dispositivo
	    DeviceRoom deviceInfo = deviceRoomRepository.findByCompanyIdAndId(empresaId, deviceRoomId)
	            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
	                    "Associação entre dispositivo e sala não encontrada"));
	    
	    if (deviceInfo == null) {
	        throw new RuntimeException("Dispositivo não encontrado ou não pertence à empresa");
	    }
	    
	    String deviceName = safeGetString(deviceInfo.getDevice().getName(), "Dispositivo Desconhecido");
	    String roomName = safeGetString(deviceInfo.getRoom().getName(), "Sala Desconhecida");
	    
	    // Buscar dados baseado na granularidade
	    List<Object[]> rawData = (granularity == Granularity.HOURLY)
	        ? consumptionRepository.findDeviceDetailHourly(empresaId, deviceRoomId, startDate, endDate)
	        : consumptionRepository.findDeviceDetailDaily(empresaId, deviceRoomId, startDate, endDate);
	    
	    // Processar dados
	    DeviceConsumptionDetailResponse.VoltageData voltageData = processVoltageData(rawData, granularity);
	    DeviceConsumptionDetailResponse.CurrentData currentData = processCurrentData(rawData, granularity);
	    DeviceConsumptionDetailResponse.PowerData powerData = processPowerData(rawData, granularity);
	    
	    return DeviceConsumptionDetailResponse.builder()
	        .deviceRoomId(deviceRoomId)
	        .deviceName(deviceName)
	        .roomName(roomName)
	        .startDate(startDate)
	        .endDate(endDate)
	        .granularity(granularity.name())
	        .voltage(voltageData)
	        .current(currentData)
	        .power(powerData)
	        .build();
	}

	private DeviceConsumptionDetailResponse.VoltageData processVoltageData(
	        List<Object[]> rawData, 
	        Granularity granularity) {
	    
	    List<DeviceConsumptionDetailResponse.DataPoint> points = new ArrayList<>();
	    
	    if (granularity == Granularity.WEEKLY || granularity == Granularity.MONTHLY) {
	        points = aggregateVoltagePoints(rawData, granularity);
	    } else {
	        for (Object[] row : rawData) {
	            LocalDateTime timestamp = extractTimestamp(row[0]);
	            if (timestamp == null) continue;
	            
	            DeviceConsumptionDetailResponse.DataPoint point = 
	                DeviceConsumptionDetailResponse.DataPoint.builder()
	                    .timestamp(timestamp)
	                    .avg(safeGetDouble(row[1], 0.0))
	                    .max(safeGetDouble(row[2], 0.0))
	                    .min(safeGetDouble(row[3], 0.0))
	                    .value(safeGetDouble(row[1], 0.0)) // avg como value padrão
	                    .build();
	            
	            points.add(point);
	        }
	    }
	    
	    DeviceConsumptionDetailResponse.Statistics stats = calculateDetailStatistics(points);
	    
	    return DeviceConsumptionDetailResponse.VoltageData.builder()
	        .points(points)
	        .statistics(stats)
	        .build();
	}

	private DeviceConsumptionDetailResponse.CurrentData processCurrentData(
	        List<Object[]> rawData, 
	        Granularity granularity) {
	    
	    List<DeviceConsumptionDetailResponse.DataPoint> points = new ArrayList<>();
	    
	    if (granularity == Granularity.WEEKLY || granularity == Granularity.MONTHLY) {
	        points = aggregateCurrentPoints(rawData, granularity);
	    } else {
	        for (Object[] row : rawData) {
	            LocalDateTime timestamp = extractTimestamp(row[0]);
	            if (timestamp == null) continue;
	            
	            DeviceConsumptionDetailResponse.DataPoint point = 
	                DeviceConsumptionDetailResponse.DataPoint.builder()
	                    .timestamp(timestamp)
	                    .avg(safeGetDouble(row[4], 0.0))
	                    .max(safeGetDouble(row[5], 0.0))
	                    .min(safeGetDouble(row[6], 0.0))
	                    .value(safeGetDouble(row[4], 0.0))
	                    .build();
	            
	            points.add(point);
	        }
	    }
	    
	    DeviceConsumptionDetailResponse.Statistics stats = calculateDetailStatistics(points);
	    
	    return DeviceConsumptionDetailResponse.CurrentData.builder()
	        .points(points)
	        .statistics(stats)
	        .build();
	}

	private DeviceConsumptionDetailResponse.PowerData processPowerData(
	        List<Object[]> rawData, 
	        Granularity granularity) {
	    
	    List<DeviceConsumptionDetailResponse.DataPoint> points = new ArrayList<>();
	    
	    if (granularity == Granularity.WEEKLY || granularity == Granularity.MONTHLY) {
	        points = aggregatePowerPoints(rawData, granularity);
	    } else {
	        for (Object[] row : rawData) {
	            LocalDateTime timestamp = extractTimestamp(row[0]);
	            if (timestamp == null) continue;
	            
	            DeviceConsumptionDetailResponse.DataPoint point = 
	                DeviceConsumptionDetailResponse.DataPoint.builder()
	                    .timestamp(timestamp)
	                    .avg(safeGetDouble(row[7], 0.0))
	                    .max(safeGetDouble(row[8], 0.0))
	                    .min(safeGetDouble(row[9], 0.0))
	                    .value(safeGetDouble(row[7], 0.0))
	                    .build();
	            
	            points.add(point);
	        }
	    }
	    
	    DeviceConsumptionDetailResponse.Statistics stats = calculateDetailStatistics(points);
	    
	    return DeviceConsumptionDetailResponse.PowerData.builder()
	        .points(points)
	        .statistics(stats)
	        .build();
	}

	// Métodos de agregação para WEEKLY e MONTHLY
	private List<DeviceConsumptionDetailResponse.DataPoint> aggregateVoltagePoints(
	        List<Object[]> rawData, 
	        Granularity granularity) {
	    
	    Map<LocalDateTime, List<Object[]>> grouped = groupByPeriod(rawData, granularity);
	    List<DeviceConsumptionDetailResponse.DataPoint> points = new ArrayList<>();
	    
	    for (Map.Entry<LocalDateTime, List<Object[]>> entry : grouped.entrySet()) {
	        double sumAvg = 0.0;
	        double maxValue = Double.NEGATIVE_INFINITY;
	        double minValue = Double.POSITIVE_INFINITY;
	        int count = 0;
	        
	        for (Object[] row : entry.getValue()) {
	            Double avg = safeGetDouble(row[1], null);
	            Double max = safeGetDouble(row[2], null);
	            Double min = safeGetDouble(row[3], null);
	            
	            if (avg != null) {
	                sumAvg += avg;
	                count++;
	            }
	            if (max != null && max > maxValue) maxValue = max;
	            if (min != null && min < minValue) minValue = min;
	        }
	        
	        if (count > 0) {
	            DeviceConsumptionDetailResponse.DataPoint point = 
	                DeviceConsumptionDetailResponse.DataPoint.builder()
	                    .timestamp(entry.getKey())
	                    .avg(sumAvg / count)
	                    .max(Double.isInfinite(maxValue) ? 0.0 : maxValue)
	                    .min(Double.isInfinite(minValue) ? 0.0 : minValue)
	                    .value(sumAvg / count)
	                    .build();
	            points.add(point);
	        }
	    }
	    
	    return points;
	}

	private List<DeviceConsumptionDetailResponse.DataPoint> aggregateCurrentPoints(
	        List<Object[]> rawData, 
	        Granularity granularity) {
	    
	    Map<LocalDateTime, List<Object[]>> grouped = groupByPeriod(rawData, granularity);
	    List<DeviceConsumptionDetailResponse.DataPoint> points = new ArrayList<>();
	    
	    for (Map.Entry<LocalDateTime, List<Object[]>> entry : grouped.entrySet()) {
	        double sumAvg = 0.0;
	        double maxValue = Double.NEGATIVE_INFINITY;
	        double minValue = Double.POSITIVE_INFINITY;
	        int count = 0;
	        
	        for (Object[] row : entry.getValue()) {
	            Double avg = safeGetDouble(row[4], null);
	            Double max = safeGetDouble(row[5], null);
	            Double min = safeGetDouble(row[6], null);
	            
	            if (avg != null) {
	                sumAvg += avg;
	                count++;
	            }
	            if (max != null && max > maxValue) maxValue = max;
	            if (min != null && min < minValue) minValue = min;
	        }
	        
	        if (count > 0) {
	            DeviceConsumptionDetailResponse.DataPoint point = 
	                DeviceConsumptionDetailResponse.DataPoint.builder()
	                    .timestamp(entry.getKey())
	                    .avg(sumAvg / count)
	                    .max(Double.isInfinite(maxValue) ? 0.0 : maxValue)
	                    .min(Double.isInfinite(minValue) ? 0.0 : minValue)
	                    .value(sumAvg / count)
	                    .build();
	            points.add(point);
	        }
	    }
	    
	    return points;
	}

	private List<DeviceConsumptionDetailResponse.DataPoint> aggregatePowerPoints(
	        List<Object[]> rawData, 
	        Granularity granularity) {
	    
	    Map<LocalDateTime, List<Object[]>> grouped = groupByPeriod(rawData, granularity);
	    List<DeviceConsumptionDetailResponse.DataPoint> points = new ArrayList<>();
	    
	    for (Map.Entry<LocalDateTime, List<Object[]>> entry : grouped.entrySet()) {
	        double sumAvg = 0.0;
	        double maxValue = Double.NEGATIVE_INFINITY;
	        double minValue = Double.POSITIVE_INFINITY;
	        int count = 0;
	        
	        for (Object[] row : entry.getValue()) {
	            Double avg = safeGetDouble(row[7], null);
	            Double max = safeGetDouble(row[8], null);
	            Double min = safeGetDouble(row[9], null);
	            
	            if (avg != null) {
	                sumAvg += avg;
	                count++;
	            }
	            if (max != null && max > maxValue) maxValue = max;
	            if (min != null && min < minValue) minValue = min;
	        }
	        
	        if (count > 0) {
	            DeviceConsumptionDetailResponse.DataPoint point = 
	                DeviceConsumptionDetailResponse.DataPoint.builder()
	                    .timestamp(entry.getKey())
	                    .avg(sumAvg / count)
	                    .max(Double.isInfinite(maxValue) ? 0.0 : maxValue)
	                    .min(Double.isInfinite(minValue) ? 0.0 : minValue)
	                    .value(sumAvg / count)
	                    .build();
	            points.add(point);
	        }
	    }
	    
	    return points;
	}

	private Map<LocalDateTime, List<Object[]>> groupByPeriod(
	        List<Object[]> rawData, 
	        Granularity granularity) {
	    
	    Map<LocalDateTime, List<Object[]>> grouped = new HashMap<>();
	    
	    for (Object[] row : rawData) {
	        LocalDateTime timestamp = extractTimestamp(row[0]);
	        if (timestamp == null) continue;
	        
	        LocalDateTime bucketKey;
	        if (granularity == Granularity.WEEKLY) {
	            bucketKey = timestamp.truncatedTo(ChronoUnit.DAYS)
	                .minusDays(timestamp.getDayOfWeek().getValue() - 1);
	        } else { // MONTHLY
	            bucketKey = timestamp.withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
	        }
	        
	        grouped.computeIfAbsent(bucketKey, k -> new ArrayList<>()).add(row);
	    }
	    
	    return grouped;
	}

	private DeviceConsumptionDetailResponse.Statistics calculateDetailStatistics(
	        List<DeviceConsumptionDetailResponse.DataPoint> points) {
	    
	    if (points == null || points.isEmpty()) {
	        return DeviceConsumptionDetailResponse.Statistics.builder()
	            .total(0.0)
	            .average(0.0)
	            .maximum(0.0)
	            .minimum(0.0)
	            .totalReadings(0L)
	            .build();
	    }
	    
	    double sum = 0.0;
	    double max = Double.NEGATIVE_INFINITY;
	    double min = Double.POSITIVE_INFINITY;
	    
	    for (DeviceConsumptionDetailResponse.DataPoint point : points) {
	        sum += point.getValue();
	        if (point.getMax() > max) max = point.getMax();
	        if (point.getMin() < min) min = point.getMin();
	    }
	    
	    return DeviceConsumptionDetailResponse.Statistics.builder()
	        .total(sum)
	        .average(sum / points.size())
	        .maximum(Double.isInfinite(max) ? 0.0 : max)
	        .minimum(Double.isInfinite(min) ? 0.0 : min)
	        .totalReadings((long) points.size())
	        .build();
	}
}