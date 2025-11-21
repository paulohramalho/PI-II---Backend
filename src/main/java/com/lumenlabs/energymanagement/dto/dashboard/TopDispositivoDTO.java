package com.lumenlabs.energymanagement.dto.dashboard;

public class TopDispositivoDTO {
	private String nome; // Nome do dispositivo
	private Double consumo; // Total kWh

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Double getConsumo() {
		return consumo;
	}

	public void setConsumo(Double consumo) {
		this.consumo = consumo;
	}

}