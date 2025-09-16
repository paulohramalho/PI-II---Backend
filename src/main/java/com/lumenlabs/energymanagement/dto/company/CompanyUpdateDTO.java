package com.lumenlabs.energymanagement.dto.company;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CompanyUpdateDTO {

	@NotBlank(message = "Nome é obrigatório")
	@Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
	private String nomeFantasia;

	@Pattern(regexp = "\\+?\\d{0,3}?\\s?\\(?\\d{2,3}\\)?\\s?\\d{4,5}-?\\d{4}", message = "Telefone inválido")
	private String telefone;

	@NotBlank(message = "Logradouro é obrigatório")
	private String logradouro;

	@NotNull(message = "Número é obrigatório")
	private Integer number;

	@NotBlank(message = "Bairro é obrigatório")
	private String bairro;

	@NotBlank(message = "CEP é obrigatório")
	private String zipCode;

	@NotBlank(message = "Cidade é obrigatória")
	private String city;

	@NotBlank(message = "UF é obrigatória")
	private String uf;

	@NotBlank(message = "Complemento é obrigatório")
	private String complemento;

	@NotBlank(message = "Nome do administrador é obrigatório")
	private String adminName;

	@NotBlank(message = "Email do administrador é obrigatório")
	@Email(message = "Email inválido")
	private String adminEmail;

	@NotBlank(message = "Senha do administrador é obrigatória")
	@Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
	private String adminPassword;

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

	public String getLogradouro() {
		return logradouro;
	}

	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	public String getAdminName() {
		return adminName;
	}

	public void setAdminName(String adminName) {
		this.adminName = adminName;
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
