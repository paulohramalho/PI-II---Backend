package com.lumenlabs.energymanagement.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.lumenlabs.energymanagement.dto.user.UserDTO;
import com.lumenlabs.energymanagement.dto.user.UserRegistrationDTO;
import com.lumenlabs.energymanagement.enums.Role;
import com.lumenlabs.energymanagement.mapper.user.UserMapper;
import com.lumenlabs.energymanagement.model.Company;
import com.lumenlabs.energymanagement.model.User;
import com.lumenlabs.energymanagement.repository.UserRepository;

@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserMapper userMapper;

	public Page<UserDTO> getUsers(UUID companyId, String name, Pageable pageable) {
		return userRepository.findAllByCompanyIdAndNameContainingIgnoreCase(companyId, name, pageable)
				.map(userMapper::mapToUserDTO);
	}

	public UserDTO getUser(UUID companyId, UUID id) {
		User user = userRepository.findByCompanyIdAndId(companyId, id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
		return userMapper.mapToUserDTO(user);
	}

	public User createUser(Company loggedUserCompany, UserRegistrationDTO userRegistrationDTO) {
		if(userRepository.existsByEmailIgnoreCase(userRegistrationDTO.getEmail()))
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Usuário já existente");
		return userRepository.save(userMapper.mapToUser(userRegistrationDTO, loggedUserCompany));
	}

	public void updateUser(Company loggedUserCompany, UserRegistrationDTO userRegistrationDTO, UUID id) {
		User user = userRepository.findByCompanyIdAndId(loggedUserCompany.getId(), id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
		if(userRepository.existsByEmailIgnoreCaseAndIdNot(userRegistrationDTO.getEmail(), user.getId()))
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Usuário já existente");
		userMapper.copyToUser(userRegistrationDTO, user);
		userRepository.save(user);
	}

	public void deleteUser(UUID companyId, UUID id) {
		User user = userRepository.findByCompanyIdAndId(companyId, id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
		if(user.getRole().equals(Role.ADMIN) && userRepository.countByRoleAndCompanyId(Role.ADMIN, companyId) < 2)
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Único administrador");
		userRepository.delete(user);
	}
	
	
}
