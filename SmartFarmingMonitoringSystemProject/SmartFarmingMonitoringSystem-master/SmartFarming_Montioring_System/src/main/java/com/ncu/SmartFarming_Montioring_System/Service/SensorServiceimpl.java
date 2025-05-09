package com.ncu.SmartFarming_Montioring_System.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ncu.SmartFarming_Montioring_System.Entity.Field;
import com.ncu.SmartFarming_Montioring_System.Entity.SensorReading;
import com.ncu.SmartFarming_Montioring_System.Exception.FieldNotFoundException;
import com.ncu.SmartFarming_Montioring_System.Exception.InvalidReadingException;
import com.ncu.SmartFarming_Montioring_System.Repositery.SensorReadingRepositery;
import com.ncu.SmartFarming_Montioring_System.Repositery.SmartFarmingRepositery;
@Service
public class SensorServiceimpl implements SensorService {
@Autowired
private SensorReadingRepositery sensorReadingRepositery;
@Autowired
private SmartFarmingRepositery smartFarmingRepositery;
@Override
public SensorReading submitReading(Long fieldId, SensorReading reading) {
	Field field = smartFarmingRepositery.findById(fieldId)
            .orElseThrow(() -> new FieldNotFoundException("Field ID " + fieldId + " not found"));

    if (reading.getSensorType() == null || reading.getValue() == null) {
        throw new InvalidReadingException("Type or value is missing");
    }

    reading.setField(field);
    reading.setTimestamp(LocalDateTime.now());
    return sensorReadingRepositery.save(reading);
}
@Override
public List<SensorReading> getAllReadingsForField(Long fieldId) {
	Field field = smartFarmingRepositery.findById(fieldId)
    .orElseThrow(() -> new FieldNotFoundException("Field ID " + fieldId + " not found"));
    return sensorReadingRepositery.findByField(field);
}
@Override
public List<SensorReading> getReadingsByTypeAndRange(Long fieldId, String type, LocalDateTime start,
		LocalDateTime end) {
	Field field = smartFarmingRepositery.findById(fieldId)
            .orElseThrow(() -> new FieldNotFoundException("Field ID " + fieldId + " not found"));

    if (type != null && start != null && end != null) {
        return sensorReadingRepositery.findByFieldAndSensorTypeAndTimestampBetween(field, type, start, end);
    }

    return sensorReadingRepositery.findByField(field);
}
@Override
public Map<String, SensorReading> getLatestReadingsPerType(Long fieldId) {
	
	Field field = smartFarmingRepositery.findById(fieldId)
            .orElseThrow(() -> new FieldNotFoundException("Field ID " + fieldId + " not found"));

    List<String> sensorTypes = sensorReadingRepositery.findDistinctSensorTypesByField(field);

    Map<String, SensorReading> latestReadings = new HashMap<>();
    for (String type : sensorTypes) {
        SensorReading latest = sensorReadingRepositery.findTopByFieldAndSensorTypeOrderByTimestampDesc(field, type);
        if (latest != null) {
            latestReadings.put(type, latest);
        }
    }

    return latestReadings;
}
@Override
public List<Field> getFieldsNeedingAttention() {
	List<Field> allFields = smartFarmingRepositery.findAll();
	List<Field> attentionFields = new ArrayList<>();

	for (Field field : allFields) {
	    SensorReading latestMoisture = sensorReadingRepositery
	        .findTopByFieldAndSensorTypeOrderByTimestampDesc(field, "temperature");

	    if (latestMoisture != null && latestMoisture.getValue() < 30) {
	        attentionFields.add(field);
	    }
	}

	return attentionFields;
}
@Override
public List<SensorReading> getAllReadings() {
	
        return sensorReadingRepositery.findAll();
    }
}

	

