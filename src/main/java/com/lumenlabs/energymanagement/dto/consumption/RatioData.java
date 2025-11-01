package com.lumenlabs.energymanagement.dto.consumption;

import java.util.UUID;

public class RatioData {
	private String name;
	private UUID entityId;
	private Double consumption;
	private Double percentage;

	public RatioData() {
	}

	public RatioData(String name, UUID entityId, Double consumption, Double percentage) {
		this.name = name;
		this.entityId = entityId;
		this.consumption = consumption;
		this.percentage = percentage;
	}

	// Getters
	public String getName() {
		return name;
	}

	public UUID getEntityId() {
		return entityId;
	}

	public Double getConsumption() {
		return consumption;
	}

	public Double getPercentage() {
		return percentage;
	}

	// Setters
	public void setName(String name) {
		this.name = name;
	}

	public void setEntityId(UUID entityId) {
		this.entityId = entityId;
	}

	public void setConsumption(Double consumption) {
		this.consumption = consumption;
	}

	public void setPercentage(Double percentage) {
		this.percentage = percentage;
	}

	// Builder
	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private String name;
		private UUID entityId;
		private Double consumption;
		private Double percentage;

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder entityId(UUID entityId) {
			this.entityId = entityId;
			return this;
		}

		public Builder consumption(Double consumption) {
			this.consumption = consumption;
			return this;
		}

		public Builder percentage(Double percentage) {
			this.percentage = percentage;
			return this;
		}

		public RatioData build() {
			return new RatioData(name, entityId, consumption, percentage);
		}
	}
}
