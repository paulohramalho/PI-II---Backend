package com.lumenlabs.energymanagement.dto.dashboard;

public class ConsumoPorHoraDTO {
    private String hora;      // "00:00", "01:00", etc
    private Double consumo;   // Total kWh naquela hora
    private Double media;     // Média histórica
    private Double maximo;    // Pico máximo
    private Double minimo;    // Pico mínimo
    
	public String getHora() {
		return hora;
	}
	public void setHora(String hora) {
		this.hora = hora;
	}
	public Double getConsumo() {
		return consumo;
	}
	public void setConsumo(Double consumo) {
		this.consumo = consumo;
	}
	public Double getMedia() {
		return media;
	}
	public void setMedia(Double media) {
		this.media = media;
	}
	public Double getMaximo() {
		return maximo;
	}
	public void setMaximo(Double maximo) {
		this.maximo = maximo;
	}
	public Double getMinimo() {
		return minimo;
	}
	public void setMinimo(Double minimo) {
		this.minimo = minimo;
	}
    
    
}