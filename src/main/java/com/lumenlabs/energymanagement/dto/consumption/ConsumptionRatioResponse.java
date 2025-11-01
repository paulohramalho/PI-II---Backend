package com.lumenlabs.energymanagement.dto.consumption;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ConsumptionRatioResponse {
	private String resourceType;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime startDate;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime endDate;

	private List<RatioData> data;
	private Double totalConsumption;

	public ConsumptionRatioResponse() {
		this.data = new ArrayList<>();
	}

	public ConsumptionRatioResponse(String resourceType, LocalDateTime startDate, LocalDateTime endDate,
			List<RatioData> data, Double totalConsumption) {
		this.resourceType = resourceType;
		this.startDate = startDate;
		this.endDate = endDate;
		this.data = data;
		this.totalConsumption = totalConsumption;
	}

	// Getters
	public String getResourceType() {
		return resourceType;
	}

	public LocalDateTime getStartDate() {
		return startDate;
	}

	public LocalDateTime getEndDate() {
		return endDate;
	}

	public List<RatioData> getData() {
		return data;
	}

	public Double getTotalConsumption() {
		return totalConsumption;
	}

	// Setters
	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	public void setStartDate(LocalDateTime startDate) {
		this.startDate = startDate;
	}

	public void setEndDate(LocalDateTime endDate) {
		this.endDate = endDate;
	}

	public void setData(List<RatioData> data) {
		this.data = data;
	}

	public void setTotalConsumption(Double totalConsumption) {
		this.totalConsumption = totalConsumption;
	}

	// Builder
	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private String resourceType;
		private LocalDateTime startDate;
		private LocalDateTime endDate;
		private List<RatioData> data = new ArrayList<>();
		private Double totalConsumption;

		public Builder resourceType(String resourceType) {
			this.resourceType = resourceType;
			return this;
		}

		public Builder startDate(LocalDateTime startDate) {
			this.startDate = startDate;
			return this;
		}

		public Builder endDate(LocalDateTime endDate) {
			this.endDate = endDate;
			return this;
		}

		public Builder data(List<RatioData> data) {
			this.data = data;
			return this;
		}

		public Builder totalConsumption(Double totalConsumption) {
			this.totalConsumption = totalConsumption;
			return this;
		}

		public ConsumptionRatioResponse build() {
			return new ConsumptionRatioResponse(resourceType, startDate, endDate, data, totalConsumption);
		}
	}
}
