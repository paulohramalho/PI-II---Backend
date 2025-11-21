package com.lumenlabs.energymanagement.dto.dashboard;

import java.util.List;

public class DashboardResponse {
	private EstatisticasDTO estatisticas;
	private List<ConsumoPorHoraDTO> consumoPorHora;
	private List<TopDispositivoDTO> topDispositivos;
	private List<ConsumoPorSetorDTO> consumoPorSetor;
	private List<UltimaLeituraDTO> ultimasLeituras;

	public EstatisticasDTO getEstatisticas() {
		return estatisticas;
	}

	public void setEstatisticas(EstatisticasDTO estatisticas) {
		this.estatisticas = estatisticas;
	}

	public List<ConsumoPorHoraDTO> getConsumoPorHora() {
		return consumoPorHora;
	}

	public void setConsumoPorHora(List<ConsumoPorHoraDTO> consumoPorHora) {
		this.consumoPorHora = consumoPorHora;
	}

	public List<TopDispositivoDTO> getTopDispositivos() {
		return topDispositivos;
	}

	public void setTopDispositivos(List<TopDispositivoDTO> topDispositivos) {
		this.topDispositivos = topDispositivos;
	}

	public List<ConsumoPorSetorDTO> getConsumoPorSetor() {
		return consumoPorSetor;
	}

	public void setConsumoPorSetor(List<ConsumoPorSetorDTO> consumoPorSetor) {
		this.consumoPorSetor = consumoPorSetor;
	}

	public List<UltimaLeituraDTO> getUltimasLeituras() {
		return ultimasLeituras;
	}

	public void setUltimasLeituras(List<UltimaLeituraDTO> ultimasLeituras) {
		this.ultimasLeituras = ultimasLeituras;
	}

}