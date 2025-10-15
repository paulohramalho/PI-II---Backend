package com.lumenlabs.energymanagement.dto.room;

import java.util.UUID;

import com.lumenlabs.energymanagement.dto.department.DepartmentDTO;

public class RoomDTO {

	private UUID id;
	private String name;
	private String description;
	private DepartmentDTO department;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public DepartmentDTO getDepartment() {
		return department;
	}

	public void setDepartment(DepartmentDTO department) {
		this.department = department;
	}

}
