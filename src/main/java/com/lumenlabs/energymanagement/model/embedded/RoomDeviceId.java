package com.lumenlabs.energymanagement.model.embedded;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;

public class RoomDeviceId implements Serializable {

	private static final long serialVersionUID = 1L;
	
    @Column(name = "fk_sala")
    private UUID roomId;

    @Column(name = "fk_dispositivo")
    private UUID deviceId;

	public RoomDeviceId() {
	}

	public RoomDeviceId(UUID roomId, UUID deviceId) {
		this.roomId = roomId;
		this.deviceId = deviceId;
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

	@Override
	public int hashCode() {
		return Objects.hash(deviceId, roomId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RoomDeviceId other = (RoomDeviceId) obj;
		return Objects.equals(deviceId, other.deviceId) && Objects.equals(roomId, other.roomId);
	}

}
