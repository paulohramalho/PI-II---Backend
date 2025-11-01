package com.lumenlabs.energymanagement.dto.consumption;

public class Statistics {
	private Double totalConsumption;
	private Double avgConsumption;
	private Double maxConsumption;
	private Double minConsumption;
	private Long totalReadings;

	public Statistics() {
	}

	public Statistics(Double totalConsumption, Double avgConsumption, Double maxConsumption, Double minConsumption,
			Long totalReadings) {
		this.totalConsumption = totalConsumption;
		this.avgConsumption = avgConsumption;
		this.maxConsumption = maxConsumption;
		this.minConsumption = minConsumption;
		this.totalReadings = totalReadings;
	}

	// Getters
	public Double getTotalConsumption() {
		return totalConsumption;
	}

	public Double getAvgConsumption() {
		return avgConsumption;
	}

	public Double getMaxConsumption() {
		return maxConsumption;
	}

	public Double getMinConsumption() {
		return minConsumption;
	}

	public Long getTotalReadings() {
		return totalReadings;
	}

	// Setters
	public void setTotalConsumption(Double totalConsumption) {
		this.totalConsumption = totalConsumption;
	}

	public void setAvgConsumption(Double avgConsumption) {
		this.avgConsumption = avgConsumption;
	}

	public void setMaxConsumption(Double maxConsumption) {
		this.maxConsumption = maxConsumption;
	}

	public void setMinConsumption(Double minConsumption) {
		this.minConsumption = minConsumption;
	}

	public void setTotalReadings(Long totalReadings) {
		this.totalReadings = totalReadings;
	}

	// Builder
	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private Double totalConsumption;
		private Double avgConsumption;
		private Double maxConsumption;
		private Double minConsumption;
		private Long totalReadings;

		public Builder totalConsumption(Double totalConsumption) {
			this.totalConsumption = totalConsumption;
			return this;
		}

		public Builder avgConsumption(Double avgConsumption) {
			this.avgConsumption = avgConsumption;
			return this;
		}

		public Builder maxConsumption(Double maxConsumption) {
			this.maxConsumption = maxConsumption;
			return this;
		}

		public Builder minConsumption(Double minConsumption) {
			this.minConsumption = minConsumption;
			return this;
		}

		public Builder totalReadings(Long totalReadings) {
			this.totalReadings = totalReadings;
			return this;
		}

		public Statistics build() {
			return new Statistics(totalConsumption, avgConsumption, maxConsumption, minConsumption, totalReadings);
		}
	}
}