package com.lumenlabs.energymanagement.dto.consumption;

import java.time.LocalDateTime;

public class ConsumptionDTO {

	private LocalDateTime timestamp;
	private double totalPotencia;
	private double avgPotencia;
	private double maxPotencia;
	private double minPotencia;

	public ConsumptionDTO(LocalDateTime timestamp, double totalPotencia, double avgPotencia, double maxPotencia,
			double minPotencia) {
		this.timestamp = timestamp;
		this.totalPotencia = totalPotencia;
		this.avgPotencia = avgPotencia;
		this.maxPotencia = maxPotencia;
		this.minPotencia = minPotencia;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public double getTotalPotencia() {
		return totalPotencia;
	}

	public void setTotalPotencia(double totalPotencia) {
		this.totalPotencia = totalPotencia;
	}

	public double getAvgPotencia() {
		return avgPotencia;
	}

	public void setAvgPotencia(double avgPotencia) {
		this.avgPotencia = avgPotencia;
	}

	public double getMaxPotencia() {
		return maxPotencia;
	}

	public void setMaxPotencia(double maxPotencia) {
		this.maxPotencia = maxPotencia;
	}

	public double getMinPotencia() {
		return minPotencia;
	}

	public void setMinPotencia(double minPotencia) {
		this.minPotencia = minPotencia;
	}

}
