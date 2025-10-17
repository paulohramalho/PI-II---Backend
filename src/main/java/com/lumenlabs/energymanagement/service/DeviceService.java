package com.lumenlabs.energymanagement.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.lumenlabs.energymanagement.dto.device.DeviceDTO;
import com.lumenlabs.energymanagement.dto.device.DeviceRegistrationDTO;
import com.lumenlabs.energymanagement.dto.device.DeviceUpdateDTO;
import com.lumenlabs.energymanagement.mapper.device.DeviceMapper;
import com.lumenlabs.energymanagement.model.Company;
import com.lumenlabs.energymanagement.model.Device;
import com.lumenlabs.energymanagement.model.DeviceType;
import com.lumenlabs.energymanagement.repository.DeviceRepository;
import com.lumenlabs.energymanagement.repository.DeviceTypeRepository;

@Service
public class DeviceService {
	
	@Autowired
	private DeviceRepository deviceRepository;
	
	@Autowired
	private DeviceTypeRepository deviceTypeRepository;
	
	@Autowired
	private DeviceMapper deviceMapper;

	public Device createDevice(Company company, DeviceRegistrationDTO deviceRegistrationDTO) {
		DeviceType deviceType = deviceTypeRepository.findByCompanyIdAndId(company.getId(), deviceRegistrationDTO.getDeviceTypeId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tipo Dispositivo não encontrado"));
		if(deviceRepository.existsByCompanyIdAndNameIgnoreCase(company.getId(), deviceRegistrationDTO.getName()))
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Dispositivo já existente");
		Device device = deviceMapper.mapToDevice(deviceRegistrationDTO, deviceType);
		device.setCompany(company);
		return deviceRepository.save(device);
	}

	public void updateDevice(Company company, DeviceUpdateDTO deviceUpdateDTO, UUID id) {
		Device device = deviceRepository.findByCompanyIdAndId(company.getId(), id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dispositivo não encontrado"));
		if(deviceRepository.existsByCompanyIdAndNameIgnoreCaseAndIdNot(company.getId(), deviceUpdateDTO.getName(), id))
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Dispositivo já existente");
		deviceMapper.copyToDevice(deviceUpdateDTO, device);
		deviceRepository.save(device);
	}

	public void deleteDevice(UUID companyId, UUID id) {
		Device device = deviceRepository.findByCompanyIdAndId(companyId, id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dispositivo não encontrado"));
		deviceRepository.delete(device);
	}

	public DeviceDTO getDevice(UUID companyId, UUID id) {
		Device device = deviceRepository.findByCompanyIdAndId(companyId, id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dispositivo não encontrado"));
		return deviceMapper.mapToDeviceDTO(device);
	}

	public Page<DeviceDTO> getAll(UUID companyId, String name, Pageable pageable) {
		return deviceRepository.findAllByCompanyIdAndNameContainingIgnoreCase(companyId, name, pageable)
				.map(deviceMapper::mapToDeviceDTO);
	}

}
