package com.lumenlabs.energymanagement.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lumenlabs.energymanagement.model.DeviceRoom;

public interface DeviceRoomRepository extends JpaRepository<DeviceRoom, UUID> {

}

