package com.lumenlabs.energymanagement.dto.dashboard;

public class EstatisticasDTO {
	private Double consumoHoje; // kWh do período selecionado
	private Double consumoHojeVariacao; // % comparado com ontem
	private Double consumoMensal; // kWh do mês atual até agora
	private Integer dispositivosAtivos; // Count de device_room
	private Double custoEstimado; // R$ estimado do mês

	public Double getConsumoHoje() {
		return consumoHoje;
	}

	public void setConsumoHoje(Double consumoHoje) {
		this.consumoHoje = consumoHoje;
	}

	public Double getConsumoHojeVariacao() {
		return consumoHojeVariacao;
	}

	public void setConsumoHojeVariacao(Double consumoHojeVariacao) {
		this.consumoHojeVariacao = consumoHojeVariacao;
	}

	public Double getConsumoMensal() {
		return consumoMensal;
	}

	public void setConsumoMensal(Double consumoMensal) {
		this.consumoMensal = consumoMensal;
	}

	public Integer getDispositivosAtivos() {
		return dispositivosAtivos;
	}

	public void setDispositivosAtivos(Integer dispositivosAtivos) {
		this.dispositivosAtivos = dispositivosAtivos;
	}

	public Double getCustoEstimado() {
		return custoEstimado;
	}

	public void setCustoEstimado(Double custoEstimado) {
		this.custoEstimado = custoEstimado;
	}

}