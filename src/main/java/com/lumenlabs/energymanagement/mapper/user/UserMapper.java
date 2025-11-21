package com.lumenlabs.energymanagement.mapper.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.lumenlabs.energymanagement.dto.user.UserDTO;
import com.lumenlabs.energymanagement.dto.user.UserRegistrationDTO;
import com.lumenlabs.energymanagement.model.Company;
import com.lumenlabs.energymanagement.model.User;

@Component
public class UserMapper {
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public UserDTO mapToUserDTO(User user) {
		UserDTO userDTO = new UserDTO();
		userDTO.setId(user.getId());
		userDTO.setEmail(user.getEmail());
		userDTO.setName(user.getName());
		userDTO.setRole(user.getRole());
		return userDTO;
	}
	
	public User mapToUser(UserRegistrationDTO userRegistrationDTO, Company company) {
		User user = new User();
		user.setName(userRegistrationDTO.getName());
		user.setEmail(userRegistrationDTO.getEmail());
		user.setPassword(passwordEncoder.encode(userRegistrationDTO.getPassword()));
		user.setRole(userRegistrationDTO.getRole());
		user.setCompany(company);
		return user;
	}
	
	public void copyToUser(UserRegistrationDTO userRegistrationDTO, User user) {
		user.setEmail(userRegistrationDTO.getEmail());
		user.setName(userRegistrationDTO.getName());
		user.setPassword(passwordEncoder.encode(userRegistrationDTO.getPassword()));
		user.setRole(userRegistrationDTO.getRole());
	}

}
