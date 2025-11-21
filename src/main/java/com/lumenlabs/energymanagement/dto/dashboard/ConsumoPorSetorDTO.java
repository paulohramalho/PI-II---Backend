package com.lumenlabs.energymanagement.dto.dashboard;

public class ConsumoPorSetorDTO {
	private String setor; // Nome do setor
	private Double consumo; // Total kWh

	public String getSetor() {
		return setor;
	}

	public void setSetor(String setor) {
		this.setor = setor;
	}

	public Double getConsumo() {
		return consumo;
	}

	public void setConsumo(Double consumo) {
		this.consumo = consumo;
	}

}