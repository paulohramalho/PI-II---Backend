package com.lumenlabs.energymanagement.dto.room;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RoomRegistrationDTO {

	@NotBlank(message = "Nome é obrigatório")
	private String name;
	private String description;
	@NotNull(message = "ID Setor é obrigatório")
	private Long departmentId;

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

	public Long getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}

}
