package com.lumenlabs.energymanagement.mapper.devicetype;

import org.springframework.stereotype.Component;

import com.lumenlabs.energymanagement.dto.devicetype.DeviceTypeDTO;
import com.lumenlabs.energymanagement.dto.devicetype.DeviceTypeRegistrationDTO;
import com.lumenlabs.energymanagement.model.DeviceType;

@Component
public class DeviceTypeMapper {

	public DeviceType mapToDeviceType(DeviceTypeRegistrationDTO deviceTypeRegistrationDTO) {
		return new DeviceType(deviceTypeRegistrationDTO.getName());
	}

	public DeviceTypeDTO mapToDeviceTypeDto(DeviceType deviceType) {
		return new DeviceTypeDTO(deviceType.getId(), deviceType.getName());
	}

	public void copyToDeviceType(DeviceTypeRegistrationDTO deviceTypeRegistrationDTO, DeviceType deviceType) {
		deviceType.setName(deviceTypeRegistrationDTO.getName());
	}

}
