package com.lumenlabs.energymanagement.dto.deviceroom;

import java.util.UUID;

import com.lumenlabs.energymanagement.dto.device.DeviceDTO;
import com.lumenlabs.energymanagement.dto.room.RoomDTO;

public class DeviceRoomDTO {

	private UUID id;
	private String alias;
	private Float averageTimeHour;
	private RoomDTO room;
	private DeviceDTO device;

	public DeviceRoomDTO(UUID id, String alias, Float averageTimeHour, RoomDTO room, DeviceDTO device) {
		this.id = id;
		this.alias = alias;
		this.averageTimeHour = averageTimeHour;
		this.room = room;
		this.device = device;
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

	public Float getAverageTimeHour() {
		return averageTimeHour;
	}

	public void setAverageTimeHour(Float averageTimeHour) {
		this.averageTimeHour = averageTimeHour;
	}

	public RoomDTO getRoom() {
		return room;
	}

	public void setRoom(RoomDTO room) {
		this.room = room;
	}

	public DeviceDTO getDevice() {
		return device;
	}

	public void setDevice(DeviceDTO device) {
		this.device = device;
	}

}
