package com.lumenlabs.energymanagement.dto.dashboard;

public class UltimaLeituraDTO {
	private String data; // "01/11/2025"
	private String hora; // "14:35:22"
	private String dispositivo; // Nome do dispositivo
	private Double corrente; // Amperes
	private Double tensao; // Volts
	private Double potenciaAtiva; // kW

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getHora() {
		return hora;
	}

	public void setHora(String hora) {
		this.hora = hora;
	}

	public String getDispositivo() {
		return dispositivo;
	}

	public void setDispositivo(String dispositivo) {
		this.dispositivo = dispositivo;
	}

	public Double getCorrente() {
		return corrente;
	}

	public void setCorrente(Double corrente) {
		this.corrente = corrente;
	}

	public Double getTensao() {
		return tensao;
	}

	public void setTensao(Double tensao) {
		this.tensao = tensao;
	}

	public Double getPotenciaAtiva() {
		return potenciaAtiva;
	}

	public void setPotenciaAtiva(Double potenciaAtiva) {
		this.potenciaAtiva = potenciaAtiva;
	}

}