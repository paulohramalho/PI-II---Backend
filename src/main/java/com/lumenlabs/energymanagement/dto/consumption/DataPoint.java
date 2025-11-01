package com.lumenlabs.energymanagement.dto.consumption;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public class DataPoint {
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime timestamp;
	private Double totalPotencia;
	private Double avgPotencia;
	private Double maxPotencia;
	private Double minPotencia;

	public DataPoint() {
	}

	public DataPoint(LocalDateTime timestamp, Double totalPotencia, Double avgPotencia, Double maxPotencia,
			Double minPotencia) {
		this.timestamp = timestamp;
		this.totalPotencia = totalPotencia;
		this.avgPotencia = avgPotencia;
		this.maxPotencia = maxPotencia;
		this.minPotencia = minPotencia;
	}

	// Getters
	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public Double getTotalPotencia() {
		return totalPotencia;
	}

	public Double getAvgPotencia() {
		return avgPotencia;
	}

	public Double getMaxPotencia() {
		return maxPotencia;
	}

	public Double getMinPotencia() {
		return minPotencia;
	}

	// Setters
	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public void setTotalPotencia(Double totalPotencia) {
		this.totalPotencia = totalPotencia;
	}

	public void setAvgPotencia(Double avgPotencia) {
		this.avgPotencia = avgPotencia;
	}

	public void setMaxPotencia(Double maxPotencia) {
		this.maxPotencia = maxPotencia;
	}

	public void setMinPotencia(Double minPotencia) {
		this.minPotencia = minPotencia;
	}

	// Builder
	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private LocalDateTime timestamp;
		private Double totalPotencia;
		private Double avgPotencia;
		private Double maxPotencia;
		private Double minPotencia;

		public Builder timestamp(LocalDateTime timestamp) {
			this.timestamp = timestamp;
			return this;
		}

		public Builder totalPotencia(Double totalPotencia) {
			this.totalPotencia = totalPotencia;
			return this;
		}

		public Builder avgPotencia(Double avgPotencia) {
			this.avgPotencia = avgPotencia;
			return this;
		}

		public Builder maxPotencia(Double maxPotencia) {
			this.maxPotencia = maxPotencia;
			return this;
		}

		public Builder minPotencia(Double minPotencia) {
			this.minPotencia = minPotencia;
			return this;
		}

		public DataPoint build() {
			return new DataPoint(timestamp, totalPotencia, avgPotencia, maxPotencia, minPotencia);
		}
	}
}
