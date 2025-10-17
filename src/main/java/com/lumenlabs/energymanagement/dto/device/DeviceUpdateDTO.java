package com.lumenlabs.energymanagement.dto.device;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DeviceUpdateDTO {

	@NotBlank(message = "Nome é obrigatório")
	private String name;
	@NotNull(message = "Potência é obrigatória")
	private BigDecimal power;

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

}
