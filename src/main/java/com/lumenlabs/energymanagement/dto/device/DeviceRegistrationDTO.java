package com.lumenlabs.energymanagement.dto.device;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DeviceRegistrationDTO {

	@NotBlank(message = "Nome é obrigatório")
	private String name;
	@NotNull(message = "Potência é obrigatória")
	private BigDecimal power;
	@NotNull(message = "ID do Tipo Dispositivo é obrigatório")
	private UUID deviceTypeId;

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

	public UUID getDeviceTypeId() {
		return deviceTypeId;
	}

	public void setDeviceTypeId(UUID deviceTypeId) {
		this.deviceTypeId = deviceTypeId;
	}

}
