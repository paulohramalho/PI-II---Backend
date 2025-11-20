package com.lumenlabs.energymanagement.dto.consumption;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class DeviceConsumptionDetailResponse {
    private UUID deviceRoomId;
    private String deviceName;
    private String roomName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String granularity;
    private VoltageData voltage;
    private CurrentData current;
    private PowerData power;
    
    // Construtores
    public DeviceConsumptionDetailResponse() {}
    
    public DeviceConsumptionDetailResponse(UUID deviceRoomId, String deviceName, String roomName, 
            LocalDateTime startDate, LocalDateTime endDate, String granularity,
            VoltageData voltage, CurrentData current, PowerData power) {
        this.deviceRoomId = deviceRoomId;
        this.deviceName = deviceName;
        this.roomName = roomName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.granularity = granularity;
        this.voltage = voltage;
        this.current = current;
        this.power = power;
    }
    
    // Getters e Setters
    public UUID getDeviceRoomId() { return deviceRoomId; }
    public void setDeviceRoomId(UUID deviceRoomId) { this.deviceRoomId = deviceRoomId; }
    
    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
    
    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }
    
    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    
    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    
    public String getGranularity() { return granularity; }
    public void setGranularity(String granularity) { this.granularity = granularity; }
    
    public VoltageData getVoltage() { return voltage; }
    public void setVoltage(VoltageData voltage) { this.voltage = voltage; }
    
    public CurrentData getCurrent() { return current; }
    public void setCurrent(CurrentData current) { this.current = current; }
    
    public PowerData getPower() { return power; }
    public void setPower(PowerData power) { this.power = power; }
    
    // Builder
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private UUID deviceRoomId;
        private String deviceName;
        private String roomName;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private String granularity;
        private VoltageData voltage;
        private CurrentData current;
        private PowerData power;
        
        public Builder deviceRoomId(UUID deviceRoomId) {
            this.deviceRoomId = deviceRoomId;
            return this;
        }
        
        public Builder deviceName(String deviceName) {
            this.deviceName = deviceName;
            return this;
        }
        
        public Builder roomName(String roomName) {
            this.roomName = roomName;
            return this;
        }
        
        public Builder startDate(LocalDateTime startDate) {
            this.startDate = startDate;
            return this;
        }
        
        public Builder endDate(LocalDateTime endDate) {
            this.endDate = endDate;
            return this;
        }
        
        public Builder granularity(String granularity) {
            this.granularity = granularity;
            return this;
        }
        
        public Builder voltage(VoltageData voltage) {
            this.voltage = voltage;
            return this;
        }
        
        public Builder current(CurrentData current) {
            this.current = current;
            return this;
        }
        
        public Builder power(PowerData power) {
            this.power = power;
            return this;
        }
        
        public DeviceConsumptionDetailResponse build() {
            return new DeviceConsumptionDetailResponse(deviceRoomId, deviceName, roomName,
                startDate, endDate, granularity, voltage, current, power);
        }
    }
    
    // ============================
    // VOLTAGE DATA
    // ============================
    public static class VoltageData {
        private List<DataPoint> points;
        private Statistics statistics;
        
        public VoltageData() {}
        
        public VoltageData(List<DataPoint> points, Statistics statistics) {
            this.points = points;
            this.statistics = statistics;
        }
        
        public List<DataPoint> getPoints() { return points; }
        public void setPoints(List<DataPoint> points) { this.points = points; }
        
        public Statistics getStatistics() { return statistics; }
        public void setStatistics(Statistics statistics) { this.statistics = statistics; }
        
        public static VoltageDataBuilder builder() {
            return new VoltageDataBuilder();
        }
        
        public static class VoltageDataBuilder {
            private List<DataPoint> points;
            private Statistics statistics;
            
            public VoltageDataBuilder points(List<DataPoint> points) {
                this.points = points;
                return this;
            }
            
            public VoltageDataBuilder statistics(Statistics statistics) {
                this.statistics = statistics;
                return this;
            }
            
            public VoltageData build() {
                return new VoltageData(points, statistics);
            }
        }
    }
    
    // ============================
    // CURRENT DATA
    // ============================
    public static class CurrentData {
        private List<DataPoint> points;
        private Statistics statistics;
        
        public CurrentData() {}
        
        public CurrentData(List<DataPoint> points, Statistics statistics) {
            this.points = points;
            this.statistics = statistics;
        }
        
        public List<DataPoint> getPoints() { return points; }
        public void setPoints(List<DataPoint> points) { this.points = points; }
        
        public Statistics getStatistics() { return statistics; }
        public void setStatistics(Statistics statistics) { this.statistics = statistics; }
        
        public static CurrentDataBuilder builder() {
            return new CurrentDataBuilder();
        }
        
        public static class CurrentDataBuilder {
            private List<DataPoint> points;
            private Statistics statistics;
            
            public CurrentDataBuilder points(List<DataPoint> points) {
                this.points = points;
                return this;
            }
            
            public CurrentDataBuilder statistics(Statistics statistics) {
                this.statistics = statistics;
                return this;
            }
            
            public CurrentData build() {
                return new CurrentData(points, statistics);
            }
        }
    }
    
    // ============================
    // POWER DATA
    // ============================
    public static class PowerData {
        private List<DataPoint> points;
        private Statistics statistics;
        
        public PowerData() {}
        
        public PowerData(List<DataPoint> points, Statistics statistics) {
            this.points = points;
            this.statistics = statistics;
        }
        
        public List<DataPoint> getPoints() { return points; }
        public void setPoints(List<DataPoint> points) { this.points = points; }
        
        public Statistics getStatistics() { return statistics; }
        public void setStatistics(Statistics statistics) { this.statistics = statistics; }
        
        public static PowerDataBuilder builder() {
            return new PowerDataBuilder();
        }
        
        public static class PowerDataBuilder {
            private List<DataPoint> points;
            private Statistics statistics;
            
            public PowerDataBuilder points(List<DataPoint> points) {
                this.points = points;
                return this;
            }
            
            public PowerDataBuilder statistics(Statistics statistics) {
                this.statistics = statistics;
                return this;
            }
            
            public PowerData build() {
                return new PowerData(points, statistics);
            }
        }
    }
    
    // ============================
    // DATA POINT
    // ============================
    public static class DataPoint {
        private LocalDateTime timestamp;
        private Double value;
        private Double avg;
        private Double max;
        private Double min;
        
        public DataPoint() {}
        
        public DataPoint(LocalDateTime timestamp, Double value, Double avg, Double max, Double min) {
            this.timestamp = timestamp;
            this.value = value;
            this.avg = avg;
            this.max = max;
            this.min = min;
        }
        
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
        
        public Double getValue() { return value; }
        public void setValue(Double value) { this.value = value; }
        
        public Double getAvg() { return avg; }
        public void setAvg(Double avg) { this.avg = avg; }
        
        public Double getMax() { return max; }
        public void setMax(Double max) { this.max = max; }
        
        public Double getMin() { return min; }
        public void setMin(Double min) { this.min = min; }
        
        public static DataPointBuilder builder() {
            return new DataPointBuilder();
        }
        
        public static class DataPointBuilder {
            private LocalDateTime timestamp;
            private Double value;
            private Double avg;
            private Double max;
            private Double min;
            
            public DataPointBuilder timestamp(LocalDateTime timestamp) {
                this.timestamp = timestamp;
                return this;
            }
            
            public DataPointBuilder value(Double value) {
                this.value = value;
                return this;
            }
            
            public DataPointBuilder avg(Double avg) {
                this.avg = avg;
                return this;
            }
            
            public DataPointBuilder max(Double max) {
                this.max = max;
                return this;
            }
            
            public DataPointBuilder min(Double min) {
                this.min = min;
                return this;
            }
            
            public DataPoint build() {
                return new DataPoint(timestamp, value, avg, max, min);
            }
        }
    }
    
    // ============================
    // STATISTICS
    // ============================
    public static class Statistics {
        private Double total;
        private Double average;
        private Double maximum;
        private Double minimum;
        private Long totalReadings;
        
        public Statistics() {}
        
        public Statistics(Double total, Double average, Double maximum, Double minimum, Long totalReadings) {
            this.total = total;
            this.average = average;
            this.maximum = maximum;
            this.minimum = minimum;
            this.totalReadings = totalReadings;
        }
        
        public Double getTotal() { return total; }
        public void setTotal(Double total) { this.total = total; }
        
        public Double getAverage() { return average; }
        public void setAverage(Double average) { this.average = average; }
        
        public Double getMaximum() { return maximum; }
        public void setMaximum(Double maximum) { this.maximum = maximum; }
        
        public Double getMinimum() { return minimum; }
        public void setMinimum(Double minimum) { this.minimum = minimum; }
        
        public Long getTotalReadings() { return totalReadings; }
        public void setTotalReadings(Long totalReadings) { this.totalReadings = totalReadings; }
        
        public static StatisticsBuilder builder() {
            return new StatisticsBuilder();
        }
        
        public static class StatisticsBuilder {
            private Double total;
            private Double average;
            private Double maximum;
            private Double minimum;
            private Long totalReadings;
            
            public StatisticsBuilder total(Double total) {
                this.total = total;
                return this;
            }
            
            public StatisticsBuilder average(Double average) {
                this.average = average;
                return this;
            }
            
            public StatisticsBuilder maximum(Double maximum) {
                this.maximum = maximum;
                return this;
            }
            
            public StatisticsBuilder minimum(Double minimum) {
                this.minimum = minimum;
                return this;
            }
            
            public StatisticsBuilder totalReadings(Long totalReadings) {
                this.totalReadings = totalReadings;
                return this;
            }
            
            public Statistics build() {
                return new Statistics(total, average, maximum, minimum, totalReadings);
            }
        }
    }
}