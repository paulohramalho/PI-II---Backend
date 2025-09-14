package com.lumenlabs.energymanagement.model;

import java.util.Objects;

import com.lumenlabs.energymanagement.model.embedded.RoomDeviceId;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;

@Table(name = "dispositivo_sala")
@Entity
public class DeviceRoom {

	@EmbeddedId
	private RoomDeviceId id;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("roomId")
	@JoinColumn(name = "fk_sala", nullable = false)
	private Room room;

	@ManyToOne(fetch = FetchType.EAGER)
	@MapsId("deviceId")
	@JoinColumn(name = "fk_dispositivo", nullable = false)
	private Device device;

	@Column(nullable = false)
	private Integer quantidade;

	@Column(name = "tempo_medio_hora", nullable = false)
	private Float averageTimeHour;

	public DeviceRoom() {
	}

	public DeviceRoom(Room room, Device device, Integer quantidade, Float averageTimeHour) {
		this.room = room;
		this.device = device;
		this.quantidade = quantidade;
		this.averageTimeHour = averageTimeHour;
	}

	public RoomDeviceId getId() {
		return id;
	}

	public void setId(RoomDeviceId id) {
		this.id = id;
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

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public Float getAverageTimeHour() {
		return averageTimeHour;
	}

	public void setAverageTimeHour(Float averageTimeHour) {
		this.averageTimeHour = averageTimeHour;
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
