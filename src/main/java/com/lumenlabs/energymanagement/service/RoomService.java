package com.lumenlabs.energymanagement.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.lumenlabs.energymanagement.dto.room.RoomDTO;
import com.lumenlabs.energymanagement.dto.room.RoomRegistrationDTO;
import com.lumenlabs.energymanagement.dto.room.RoomUpdateDTO;
import com.lumenlabs.energymanagement.mapper.room.RoomMapper;
import com.lumenlabs.energymanagement.model.Company;
import com.lumenlabs.energymanagement.model.Department;
import com.lumenlabs.energymanagement.model.Room;
import com.lumenlabs.energymanagement.repository.DepartmentRepository;
import com.lumenlabs.energymanagement.repository.RoomRepository;

@Service
public class RoomService {

	@Autowired
	private RoomRepository roomRepository;
	
	@Autowired
	private DepartmentRepository departmentRepository;

	@Autowired
	private RoomMapper roomMapper;
	
	public Page<RoomDTO> getRoomByDepartment(UUID companyId, UUID departmentId, String name, Pageable pageable){
		if(!departmentRepository.existsByCompanyIdAndId(companyId, departmentId))
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Setor não encontrado");
		return roomRepository.findAllByDepartmentIdAndCompanyIdAndNameContainingIgnoreCase(departmentId, companyId, name, pageable)
				.map(roomMapper::mapToRoomDTO);
	}

	public Room createRoom(Company company, RoomRegistrationDTO roomRegistrationDTO) {
		Department department = departmentRepository.findByCompanyIdAndId(company.getId(), roomRegistrationDTO.getDepartmentId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Setor não encontrado"));
		if(roomRepository.existsByCompanyIdAndDepartmentIdAndNameIgnoreCase(company.getId(), department.getId(), roomRegistrationDTO.getName()))
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Sala já existente no setor");
		Room room = roomMapper.mapToRoom(roomRegistrationDTO, department);
		room.setCompany(company);
		return roomRepository.save(room);
	}

	public RoomDTO getRoom(UUID companyId, UUID id) {
		Room room = roomRepository.findByCompanyIdAndId(companyId, id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sala não encontrada"));
		return roomMapper.mapToRoomDTO(room);
	}

	public void updateRoom(Company company, RoomUpdateDTO roomRegistrationDTO, UUID id) {
		Room room = roomRepository.findByCompanyIdAndId(company.getId(), id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sala não encontrada"));
		if(roomRepository.existsByCompanyIdAndDepartmentIdAndNameIgnoreCaseAndIdNot(company.getId(), room.getDepartment().getId(), roomRegistrationDTO.getName(), id))
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Sala já existente");
		roomMapper.copyToRoom(roomRegistrationDTO, room);
		roomRepository.save(room);
	}

	public void deleteRoom(UUID companyId, UUID id) {
		Room room = roomRepository.findByCompanyIdAndId(companyId, id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sala não encontrado"));
		roomRepository.delete(room);
	}

	public Page<RoomDTO> getAll(UUID companyId, String name, Pageable pageable) {
		return roomRepository.findAllByCompanyIdAndNameContainingIgnoreCase(companyId, name, pageable)
				.map(roomMapper::mapToRoomDTO);
	}
	
}
