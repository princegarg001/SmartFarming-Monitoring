package com.ncu.SmartFarming_Montioring_System.Repositery;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ncu.SmartFarming_Montioring_System.Entity.Field;
import com.ncu.SmartFarming_Montioring_System.Entity.SensorReading;
@Repository
public interface SensorReadingRepositery extends  JpaRepository<SensorReading, Long> {
	
	List<SensorReading> findByField(Field field);

    List<SensorReading> findByFieldAndSensorType(Field field, String sensorType);

    List<SensorReading> findByFieldAndSensorTypeAndTimestampBetween(
            Field field, String sensorType, LocalDateTime start, LocalDateTime end);

    SensorReading findTopByFieldAndSensorTypeOrderByTimestampDesc(Field field, String sensorType);

    @Query("SELECT DISTINCT r.sensorType FROM SensorReading r WHERE r.field = :field")
    List<String> findDistinctSensorTypesByField(@Param("field") Field field);
    
    @Query("SELECT sr FROM SensorReading sr WHERE sr.sensorType = 'moisture' AND sr.timestamp = " +
    	       "(SELECT MAX(s.timestamp) FROM SensorReading s WHERE s.field = sr.field AND s.sensorType = 'temperature')")
    	List<SensorReading> findFieldsNeedingAttention();
}

