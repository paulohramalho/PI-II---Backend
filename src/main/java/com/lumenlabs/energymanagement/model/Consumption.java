package com.lumenlabs.energymanagement.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Table(name = "consumo")
@Entity
public class Consumption {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;
	@Id
	@Column(name = "event_time", nullable = false)
	private LocalDateTime eventTime;
	@Column(nullable = false)
	private float corrente;
	@Column(nullable = false)
	private float tensao;
	@Column(name = "potencia_ativa", nullable = false)
	private float potenciaAtiva;
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_dispositivo_sala", nullable = false)
	private DeviceRoom deviceRoom;

	public Consumption() {
	}

	public Consumption(LocalDateTime eventTime, float corrente, float tensao, float potenciaAtiva,
			DeviceRoom deviceRoom) {
		this.eventTime = eventTime;
		this.corrente = corrente;
		this.tensao = tensao;
		this.potenciaAtiva = potenciaAtiva;
		this.deviceRoom = deviceRoom;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public LocalDateTime getEventTime() {
		return eventTime;
	}

	public void setEventTime(LocalDateTime eventTime) {
		this.eventTime = eventTime;
	}

	public float getCorrente() {
		return corrente;
	}

	public void setCorrente(float corrente) {
		this.corrente = corrente;
	}

	public float getTensao() {
		return tensao;
	}

	public void setTensao(float tensao) {
		this.tensao = tensao;
	}

	public float getPotenciaAtiva() {
		return potenciaAtiva;
	}

	public void setPotenciaAtiva(float potenciaAtiva) {
		this.potenciaAtiva = potenciaAtiva;
	}

	public DeviceRoom getDevice() {
		return deviceRoom;
	}

	public void setDevice(DeviceRoom deviceRoom) {
		this.deviceRoom = deviceRoom;
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
		Consumption other = (Consumption) obj;
		return Objects.equals(id, other.id);
	}

}
