package com.lumenlabs.energymanagement.model;

import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.FetchType;

@Table(name = "sala",
uniqueConstraints = @UniqueConstraint(columnNames = {"nome", "fk_setor"}))
@Entity
public class Room {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;
	@Column(name = "nome", nullable = false, length = 100)
	private String name;
	@Column(name = "descricao", length = 150)
	private String description;
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "fk_setor", nullable = false)
	private Department department;
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_empresa", nullable = false)
	private Company company;

	public Room() {
	}

	public Room(String name, String description, Department department) {
		this.name = name;
		this.description = description;
		this.department = department;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Room other = (Room) obj;
		return Objects.equals(id, other.id);
	}

}
