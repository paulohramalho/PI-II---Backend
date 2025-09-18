package com.lumenlabs.energymanagement.mapper.room;

import org.springframework.stereotype.Component;

import com.lumenlabs.energymanagement.dto.room.RoomDTO;
import com.lumenlabs.energymanagement.dto.room.RoomRegistrationDTO;
import com.lumenlabs.energymanagement.dto.room.RoomUpdateDTO;
import com.lumenlabs.energymanagement.model.Department;
import com.lumenlabs.energymanagement.model.Room;

@Component
public class RoomMapper {

	public RoomDTO mapToRoomDTO(Room room) {
		RoomDTO dto = new RoomDTO();
		dto.setDescription(room.getDescription());
		dto.setName(room.getName());
		dto.setId(room.getId());
		return dto;
	}

	public Room mapToRoom(RoomRegistrationDTO dto, Department department) {
		Room room = new Room();
		room.setDepartment(department);
		room.setDescription(dto.getDescription());
		room.setName(dto.getName());
		return room;
	}
	
	public void copyToRoom(RoomUpdateDTO dto, Room room) {
		room.setDescription(dto.getDescription());
		room.setName(dto.getName());
	}
	
}
