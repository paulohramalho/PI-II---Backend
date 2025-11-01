package com.lumenlabs.energymanagement.dto.consumption;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ConsumptionEvolutionResponse {
	private String resourceType;
	private String granularity;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime startDate;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime endDate;

	private List<SeriesData> series;

	public ConsumptionEvolutionResponse() {
		this.series = new ArrayList<>();
	}

	public ConsumptionEvolutionResponse(String resourceType, String granularity, LocalDateTime startDate,
			LocalDateTime endDate, List<SeriesData> series) {
		this.resourceType = resourceType;
		this.granularity = granularity;
		this.startDate = startDate;
		this.endDate = endDate;
		this.series = series;
	}

	// Getters
	public String getResourceType() {
		return resourceType;
	}

	public String getGranularity() {
		return granularity;
	}

	public LocalDateTime getStartDate() {
		return startDate;
	}

	public LocalDateTime getEndDate() {
		return endDate;
	}

	public List<SeriesData> getSeries() {
		return series;
	}

	// Setters
	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	public void setGranularity(String granularity) {
		this.granularity = granularity;
	}

	public void setStartDate(LocalDateTime startDate) {
		this.startDate = startDate;
	}

	public void setEndDate(LocalDateTime endDate) {
		this.endDate = endDate;
	}

	public void setSeries(List<SeriesData> series) {
		this.series = series;
	}

	// Builder
	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private String resourceType;
		private String granularity;
		private LocalDateTime startDate;
		private LocalDateTime endDate;
		private List<SeriesData> series = new ArrayList<>();

		public Builder resourceType(String resourceType) {
			this.resourceType = resourceType;
			return this;
		}

		public Builder granularity(String granularity) {
			this.granularity = granularity;
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

		public Builder series(List<SeriesData> series) {
			this.series = series;
			return this;
		}

		public ConsumptionEvolutionResponse build() {
			return new ConsumptionEvolutionResponse(resourceType, granularity, startDate, endDate, series);
		}
	}
}