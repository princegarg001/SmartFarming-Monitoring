package com.ncu.SmartFarming_Montioring_System.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.ncu.SmartFarming_Montioring_System.Entity.Field;
import com.ncu.SmartFarming_Montioring_System.Entity.SensorReading;

public interface SensorService {
	
	SensorReading submitReading(Long fieldId, SensorReading reading);
    List<SensorReading> getAllReadingsForField(Long fieldId);
    List<SensorReading> getReadingsByTypeAndRange(Long fieldId, String type, LocalDateTime start, LocalDateTime end);
    Map<String, SensorReading> getLatestReadingsPerType(Long fieldId);
    List<Field> getFieldsNeedingAttention();
    List<SensorReading> getAllReadings();
}

