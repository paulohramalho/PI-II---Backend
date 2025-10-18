package com.lumenlabs.energymanagement.dto.deviceroom;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DeviceRoomUpdateDTO {

	@NotBlank(message = "Apelido é obrigatório")
	private String alias;
	@NotNull(message = "Tempo de funcionamento diario (horas) é obrigatório")
	private Float averageTimeHour;

	public DeviceRoomUpdateDTO(String alias, Float averageTimeHour) {
		this.alias = alias;
		this.averageTimeHour = averageTimeHour;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public Float getAverageTimeHour() {
		return averageTimeHour;
	}

	public void setAverageTimeHour(Float averageTimeHour) {
		this.averageTimeHour = averageTimeHour;
	}

}
