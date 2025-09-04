package com.lumenlabs.energymanagement.dto.security;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CompanyRegistrationDTO {

	@NotBlank(message = "Razão social é obrigatória")
	@Size(max = 100, message = "Razão social deve ter no máximo 100 caracteres")
	private String razaoSocial;

	@NotBlank(message = "Nome Fantasia é obrigatório")
	@Size(max = 100, message = "Nome fantasia deve ter no máximo 100 caracteres")
	private String nomeFantasia;

	@Pattern(regexp = "\\+?\\d{0,3}?\\s?\\(?\\d{2,3}\\)?\\s?\\d{4,5}-?\\d{4}", message = "Telefone inválido")
	private String telefone;

	@NotBlank(message = "Email do administrador é obrigatório")
	@Email(message = "Email inválido")
	private String adminEmail;

	@NotBlank(message = "Senha do administrador é obrigatória")
	@Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
	private String adminPassword;

	public String getRazaoSocial() {
		return razaoSocial;
	}

	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}

	public String getNomeFantasia() {
		return nomeFantasia;
	}

	public void setNomeFantasia(String nomeFantasia) {
		this.nomeFantasia = nomeFantasia;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getAdminEmail() {
		return adminEmail;
	}

	public void setAdminEmail(String adminEmail) {
		this.adminEmail = adminEmail;
	}

	public String getAdminPassword() {
		return adminPassword;
	}

	public void setAdminPassword(String adminPassword) {
		this.adminPassword = adminPassword;
	}

}
