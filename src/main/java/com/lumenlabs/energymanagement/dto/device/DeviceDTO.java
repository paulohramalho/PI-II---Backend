package com.lumenlabs.energymanagement.dto.device;

import java.math.BigDecimal;
import java.util.UUID;

import com.lumenlabs.energymanagement.dto.devicetype.DeviceTypeDTO;

public class DeviceDTO {

	private UUID id;
	private String name;
	private BigDecimal power;
	private DeviceTypeDTO deviceType;

	public DeviceDTO(UUID id, String name, BigDecimal power, DeviceTypeDTO deviceType) {
		this.id = id;
		this.name = name;
		this.power = power;
		this.deviceType = deviceType;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getPower() {
		return power;
	}

	public void setPower(BigDecimal power) {
		this.power = power;
	}

	public DeviceTypeDTO getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(DeviceTypeDTO deviceType) {
		this.deviceType = deviceType;
	}

}
