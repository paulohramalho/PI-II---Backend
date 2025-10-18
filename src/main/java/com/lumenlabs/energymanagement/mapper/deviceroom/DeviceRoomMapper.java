package com.lumenlabs.energymanagement.mapper.deviceroom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lumenlabs.energymanagement.dto.deviceroom.DeviceRoomDTO;
import com.lumenlabs.energymanagement.dto.deviceroom.DeviceRoomRegistrationDTO;
import com.lumenlabs.energymanagement.dto.deviceroom.DeviceRoomUpdateDTO;
import com.lumenlabs.energymanagement.mapper.device.DeviceMapper;
import com.lumenlabs.energymanagement.mapper.room.RoomMapper;
import com.lumenlabs.energymanagement.model.Device;
import com.lumenlabs.energymanagement.model.DeviceRoom;
import com.lumenlabs.energymanagement.model.Room;

@Component
public class DeviceRoomMapper {

	@Autowired
	private RoomMapper roomMapper;

	@Autowired
	private DeviceMapper deviceMapper;

	public DeviceRoom mapToDeviceRoom(DeviceRoomRegistrationDTO deviceRoomRegistrationDTO, Room room, Device device) {
		return new DeviceRoom(deviceRoomRegistrationDTO.getAlias(), room, device,
				deviceRoomRegistrationDTO.getAverageTimeHour());
	}

	public DeviceRoomDTO mapToDeviceRoomDTO(DeviceRoom deviceRoom) {
		return new DeviceRoomDTO(deviceRoom.getId(), deviceRoom.getAlias(), deviceRoom.getAverageTimeHour(),
				roomMapper.mapToRoomDTO(deviceRoom.getRoom()), deviceMapper.mapToDeviceDTO(deviceRoom.getDevice()));
	}
	
	public void copyToDeviceRoom(DeviceRoomUpdateDTO deviceRoomUpdateDTO, DeviceRoom deviceRoom) {
		deviceRoom.setAlias(deviceRoomUpdateDTO.getAlias());
		deviceRoom.setAverageTimeHour(deviceRoomUpdateDTO.getAverageTimeHour());
	}

}
