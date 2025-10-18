package com.lumenlabs.energymanagement.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.lumenlabs.energymanagement.dto.deviceroom.DeviceRoomDTO;
import com.lumenlabs.energymanagement.dto.deviceroom.DeviceRoomRegistrationDTO;
import com.lumenlabs.energymanagement.dto.deviceroom.DeviceRoomUpdateDTO;
import com.lumenlabs.energymanagement.mapper.deviceroom.DeviceRoomMapper;
import com.lumenlabs.energymanagement.model.Company;
import com.lumenlabs.energymanagement.model.Device;
import com.lumenlabs.energymanagement.model.DeviceRoom;
import com.lumenlabs.energymanagement.model.Room;
import com.lumenlabs.energymanagement.repository.DepartmentRepository;
import com.lumenlabs.energymanagement.repository.DeviceRepository;
import com.lumenlabs.energymanagement.repository.DeviceRoomRepository;
import com.lumenlabs.energymanagement.repository.RoomRepository;

@Service
public class DeviceRoomService {

	@Autowired
	private DeviceRoomRepository deviceRoomRepository;
	
	@Autowired
	private RoomRepository roomRepository;
	
	@Autowired
	private DepartmentRepository departmentRepository;
	
	@Autowired
	private DeviceRepository deviceRepository;

	@Autowired
	private DeviceRoomMapper deviceRoomMapper;

	public DeviceRoom createDeviceRoom(Company company, DeviceRoomRegistrationDTO deviceRoomRegistrationDTO) {
		Room room = roomRepository.findByCompanyIdAndId(company.getId(), deviceRoomRegistrationDTO.getRoomId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sala não encontrada"));
		Device device = deviceRepository.findByCompanyIdAndId(company.getId(), deviceRoomRegistrationDTO.getDeviceId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dispositivo não encontrado"));
		if(deviceRoomRepository.existsByCompanyIdAndRoomIdAndAliasIgnoreCase(company.getId(), room.getId(), deviceRoomRegistrationDTO.getAlias()))
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Apelido já existente na sala");
		DeviceRoom deviceRoom = deviceRoomMapper.mapToDeviceRoom(deviceRoomRegistrationDTO, room, device);
		deviceRoom.setCompany(company);
		return deviceRoomRepository.save(deviceRoom);
	}

	public DeviceRoomDTO getDeviceRoom(UUID companyId, UUID id) {
		DeviceRoom deviceRoom = deviceRoomRepository.findByCompanyIdAndId(companyId, id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Associação entre dispositivo e sala não encontrada"));
		return deviceRoomMapper.mapToDeviceRoomDTO(deviceRoom);
	}

	public void updateDeviceRoom(Company company, DeviceRoomUpdateDTO deviceRoomUpdateDTO, UUID id) {
		DeviceRoom deviceRoom = deviceRoomRepository.findByCompanyIdAndId(company.getId(), id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Associação entre dispositivo e sala não encontrada"));
		if(deviceRoomRepository.existsByCompanyIdAndRoomIdAndAliasIgnoreCaseAndIdNot(company.getId(), deviceRoom.getRoom().getId(), deviceRoomUpdateDTO.getAlias(), deviceRoom.getId()))
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Apelido já existente na sala");
		deviceRoomMapper.copyToDeviceRoom(deviceRoomUpdateDTO, deviceRoom);
		deviceRoomRepository.save(deviceRoom);
	}

	public void deleteDeviceRoom(UUID companyId, UUID id) {
		DeviceRoom deviceRoom = deviceRoomRepository.findByCompanyIdAndId(companyId, id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Associação entre dispositivo e sala não encontrada"));
		deviceRoomRepository.delete(deviceRoom);
	}

	public Page<DeviceRoomDTO> getAll(UUID companyId, String alias, Pageable pageable) {
		return deviceRoomRepository.findAllByCompanyIdAndAliasContainingIgnoreCase(companyId, alias, pageable)
				.map(deviceRoomMapper::mapToDeviceRoomDTO);
	}
	
	public Page<DeviceRoomDTO> getDeviceRoomByRoom(UUID companyId, UUID roomId, String alias, Pageable pageable){
		if(!roomRepository.existsByCompanyIdAndId(companyId, roomId))
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sala não encontrada");
		return deviceRoomRepository.findAllByRoomIdAndCompanyIdAndAliasContainingIgnoreCase(roomId, companyId, alias, pageable)
				.map(deviceRoomMapper::mapToDeviceRoomDTO);
	}
	
	public Page<DeviceRoomDTO> getDeviceRoomByDepartment(UUID companyId, UUID departmentId, String alias, Pageable pageable){
		if(!departmentRepository.existsByCompanyIdAndId(companyId, departmentId))
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Setor não encontrado");
		return deviceRoomRepository.findAllByRoomDepartmentIdAndCompanyIdAndAliasContainingIgnoreCase(departmentId, companyId, alias, pageable)
				.map(deviceRoomMapper::mapToDeviceRoomDTO);
	}
	
}
