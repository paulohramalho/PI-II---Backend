package com.lumenlabs.energymanagement.model.embedded;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;

public class RoomDeviceId implements Serializable {

	private static final long serialVersionUID = 1L;
	
    @Column(name = "fk_sala")
    private Long roomId;

    @Column(name = "fk_dispositivo")
    private Long deviceId;

	public RoomDeviceId() {
	}

	public RoomDeviceId(Long roomId, Long deviceId) {
		this.roomId = roomId;
		this.deviceId = deviceId;
	}

	public Long getRoomId() {
		return roomId;
	}

	public void setRoomId(Long roomId) {
		this.roomId = roomId;
	}

	public Long getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(Long deviceId) {
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
