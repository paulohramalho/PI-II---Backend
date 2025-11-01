package com.lumenlabs.energymanagement.dto.consumption;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SeriesData {
	private String name;
	private UUID entityId;
	private List<DataPoint> points;
	private Statistics statistics;

	public SeriesData() {
		this.points = new ArrayList<>();
	}

	public SeriesData(String name, UUID entityId, List<DataPoint> points, Statistics statistics) {
		this.name = name;
		this.entityId = entityId;
		this.points = points;
		this.statistics = statistics;
	}

	// Getters
	public String getName() {
		return name;
	}

	public UUID getEntityId() {
		return entityId;
	}

	public List<DataPoint> getPoints() {
		return points;
	}

	public Statistics getStatistics() {
		return statistics;
	}

	// Setters
	public void setName(String name) {
		this.name = name;
	}

	public void setEntityId(UUID entityId) {
		this.entityId = entityId;
	}

	public void setPoints(List<DataPoint> points) {
		this.points = points;
	}

	public void setStatistics(Statistics statistics) {
		this.statistics = statistics;
	}

	// Builder
	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private String name;
		private UUID entityId;
		private List<DataPoint> points = new ArrayList<>();
		private Statistics statistics;

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder entityId(UUID entityId) {
			this.entityId = entityId;
			return this;
		}

		public Builder points(List<DataPoint> points) {
			this.points = points;
			return this;
		}

		public Builder statistics(Statistics statistics) {
			this.statistics = statistics;
			return this;
		}

		public SeriesData build() {
			return new SeriesData(name, entityId, points, statistics);
		}
	}
}