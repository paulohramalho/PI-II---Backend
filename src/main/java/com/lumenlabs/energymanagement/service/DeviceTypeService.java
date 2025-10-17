package com.lumenlabs.energymanagement.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.lumenlabs.energymanagement.dto.devicetype.DeviceTypeDTO;
import com.lumenlabs.energymanagement.dto.devicetype.DeviceTypeRegistrationDTO;
import com.lumenlabs.energymanagement.mapper.devicetype.DeviceTypeMapper;
import com.lumenlabs.energymanagement.model.Company;
import com.lumenlabs.energymanagement.model.DeviceType;
import com.lumenlabs.energymanagement.repository.DeviceTypeRepository;

import jakarta.validation.Valid;

@Service
public class DeviceTypeService {

	@Autowired
	private DeviceTypeRepository deviceTypeRepository;
	
	@Autowired
	private DeviceTypeMapper deviceTypeMapper;
	
	public Page<DeviceTypeDTO> getAll(UUID companyId, String name, Pageable pageable){
		return deviceTypeRepository.findAllByCompanyIdAndNameContainingIgnoreCase(companyId, name, pageable)
				.map(deviceTypeMapper::mapToDeviceTypeDto);
	}
	
	public DeviceTypeDTO getDeviceType(UUID companyId, UUID deviceTypeId) {
		DeviceType deviceType = deviceTypeRepository.findByCompanyIdAndId(companyId, deviceTypeId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tipo Dispositivo não encontrado"));
		return deviceTypeMapper.mapToDeviceTypeDto(deviceType);
	}
	
	public DeviceType create(Company company, DeviceTypeRegistrationDTO deviceTypeRegistrationDTO) {
		if(deviceTypeRepository.existsByCompanyIdAndNameIgnoreCase(company.getId(), deviceTypeRegistrationDTO.getName()))
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Tipo Dispositivo já existente");
		DeviceType deviceType = deviceTypeMapper.mapToDeviceType(deviceTypeRegistrationDTO);
		deviceType.setCompany(company);
		return deviceTypeRepository.save(deviceType);
	}

	public void updateDeviceType(Company company, @Valid DeviceTypeRegistrationDTO deviceTypeRegistrationDTO,
			UUID id) {
		DeviceType deviceType = deviceTypeRepository.findByCompanyIdAndId(company.getId(), id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tipo Dispositivo não encontrado"));
		if(deviceTypeRepository.existsByCompanyIdAndNameIgnoreCaseAndIdNot(company.getId(), deviceTypeRegistrationDTO.getName(), id))
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Tipo Dispositivo já existente");
		deviceTypeMapper.copyToDeviceType(deviceTypeRegistrationDTO, deviceType);
		deviceTypeRepository.save(deviceType);
	}

	public void deleteDeviceType(UUID companyId, UUID id) {
		DeviceType deviceType = deviceTypeRepository.findByCompanyIdAndId(companyId, id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tipo Dispositivo não encontrado"));
		deviceTypeRepository.delete(deviceType);
	}
	
}
