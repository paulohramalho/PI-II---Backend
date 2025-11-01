package com.lumenlabs.energymanagement.model.embeddable;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class ConsumptionId implements Serializable {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private UUID id;
    private LocalDateTime eventTime;
    
    // Construtor vazio obrigatório
    public ConsumptionId() {}
    
    public ConsumptionId(UUID id, LocalDateTime eventTime) {
        this.id = id;
        this.eventTime = eventTime;
    }
    
    // Getters e Setters
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
    
    // equals e hashCode são OBRIGATÓRIOS
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConsumptionId that = (ConsumptionId) o;
        return Objects.equals(id, that.id) && 
               Objects.equals(eventTime, that.eventTime);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, eventTime);
    }
}