package com.lumenlabs.energymanagement.dto.deviceroom;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DeviceRoomRegistrationDTO {

	@NotBlank(message = "Apelido é obrigatório")
	private String alias;
	@NotNull(message = "Tempo de funcionamento diario (horas) é obrigatório")
	private Float averageTimeHour;
	@NotNull(message = "ID da sala é obrigatório")
	private UUID roomId;
	@NotNull(message = "ID do dispositivo é obrigatório")
	private UUID deviceId;

	public DeviceRoomRegistrationDTO(String alias, Float averageTimeHour, UUID roomId, UUID deviceId) {
		this.alias = alias;
		this.averageTimeHour = averageTimeHour;
		this.roomId = roomId;
		this.deviceId = deviceId;
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

	public UUID getRoomId() {
		return roomId;
	}

	public void setRoomId(UUID roomId) {
		this.roomId = roomId;
	}

	public UUID getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(UUID deviceId) {
		this.deviceId = deviceId;
	}

}
