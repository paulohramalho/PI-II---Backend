package com.lumenlabs.energymanagement.model;

import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Table(name = "dispositivo_sala",
	uniqueConstraints = @UniqueConstraint(columnNames = {"apelido", "fk_sala"}))
@Entity
public class DeviceRoom {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;

	@Column(name = "apelido", length = 100, nullable = false)
	private String alias;

	@Column(name = "tempo_medio_hora", nullable = false)
	private Float averageTimeHour;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_sala", nullable = false)
	private Room room;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "fk_dispositivo", nullable = false)
	private Device device;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_empresa", nullable = false)
	private Company company;

	public DeviceRoom() {
	}

	public DeviceRoom(String alias, Room room, Device device, Float averageTimeHour) {
		this.alias = alias;
		this.room = room;
		this.device = device;
		this.averageTimeHour = averageTimeHour;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public Float getAverageTimeHour() {
		return averageTimeHour;
	}

	public void setAverageTimeHour(Float averageTimeHour) {
		this.averageTimeHour = averageTimeHour;
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
		DeviceRoom other = (DeviceRoom) obj;
		return Objects.equals(id, other.id);
	}

}
