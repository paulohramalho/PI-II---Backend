package com.lumenlabs.energymanagement.dto.room;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RoomRegistrationDTO {

	@NotBlank(message = "Nome é obrigatório")
	private String name;
	private String description;
	@NotNull(message = "Setor é obrigatório")
	private UUID departmentId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public UUID getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(UUID departmentId) {
		this.departmentId = departmentId;
	}

}
