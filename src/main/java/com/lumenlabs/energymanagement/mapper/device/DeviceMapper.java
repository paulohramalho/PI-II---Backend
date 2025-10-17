package com.lumenlabs.energymanagement.mapper.device;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lumenlabs.energymanagement.dto.device.DeviceDTO;
import com.lumenlabs.energymanagement.dto.device.DeviceRegistrationDTO;
import com.lumenlabs.energymanagement.dto.device.DeviceUpdateDTO;
import com.lumenlabs.energymanagement.mapper.devicetype.DeviceTypeMapper;
import com.lumenlabs.energymanagement.model.Device;
import com.lumenlabs.energymanagement.model.DeviceType;

@Component
public class DeviceMapper {
	
	@Autowired
	private DeviceTypeMapper deviceTypeMapper;

	public Device mapToDevice(DeviceRegistrationDTO deviceRegistrationDTO, DeviceType deviceType) {
		return new Device(deviceRegistrationDTO.getName(), 
				deviceRegistrationDTO.getPower(), 
				deviceType);
	}

	public void copyToDevice(DeviceUpdateDTO deviceUpdateDTO, Device device) {
		device.setName(deviceUpdateDTO.getName());
		device.setPower(deviceUpdateDTO.getPower());
	}

	public DeviceDTO mapToDeviceDTO(Device device) {
		return new DeviceDTO(device.getId(), 
				device.getName(), 
				device.getPower(), 
				deviceTypeMapper.mapToDeviceTypeDto(device.getDeviceType()));
	}

}
