package com.lumenlabs.energymanagement.model;

import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Table(name = "endereco")
@Entity
public class Address {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;
	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "fk_empresa")
	private Company company;
	@Column(length = 150, nullable = false)
	private String logradouro;
	@Column(name = "numero", nullable = false)
	private Integer number;
	@Column(nullable = false, length = 100)
	private String bairro;
	@Column(name = "cep", nullable = false, length = 8)
	private String zipCode;
	@Column(name = "cidade", nullable = false, length = 100)
	private String city;
	@Column(name = "uf", length = 2, nullable = false)
	private String uf;
	@Column
	private String complemento;

	public Address() {
	}

	public Address(String logradouro, Integer number, String bairro, String zipCode, String city, String uf,
			String complemento) {
		this.logradouro = logradouro;
		this.number = number;
		this.bairro = bairro;
		this.zipCode = zipCode;
		this.city = city;
		this.uf = uf;
		this.complemento = complemento;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
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

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Address other = (Address) obj;
		return Objects.equals(id, other.id);
	}
}
