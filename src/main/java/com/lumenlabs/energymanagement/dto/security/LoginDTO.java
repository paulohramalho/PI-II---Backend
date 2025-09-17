package com.lumenlabs.energymanagement.dto.security;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginDTO {

	@NotBlank
	@Email(message = "Email inválido")
	private String email;
	@NotBlank
	@Size(max = 50)
	private String password;

	public LoginDTO() {
	}

	public LoginDTO(@NotBlank String email, @NotBlank String password) {
		this.email = email;
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
