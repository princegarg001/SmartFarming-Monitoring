package com.ncu.SmartFarming_Montioring_System;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ncu.SmartFarming_Montioring_System.Entity.Field;
import com.ncu.SmartFarming_Montioring_System.Entity.SensorReading;
import com.ncu.SmartFarming_Montioring_System.Repositery.SensorReadingRepositery;
import com.ncu.SmartFarming_Montioring_System.Repositery.SmartFarmingRepositery;
import com.ncu.SmartFarming_Montioring_System.Service.SensorServiceimpl;

public class SensorServiceTesting {
	
	@InjectMocks
	private SensorServiceimpl sensorService;

    @Mock
    private SmartFarmingRepositery fieldRepo;

    @Mock
    private SensorReadingRepositery readingRepo;

   

    private Field field;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        field = new Field();
        field.setId(1L);
        field.setName("Test Field");
        field.setMoistureThreshold(30.0);

        // Mocks
        when(fieldRepo.save(field)).thenReturn(field);
        when(fieldRepo.findById(1L)).thenReturn(Optional.of(field)); 
    }

    @Test
    void testSubmitReading_and_AlertTriggered() {
        SensorReading reading = new SensorReading();
        reading.setSensorType("temperature");
        reading.setValue(25.0); 
        reading.setField(field);
        when(readingRepo.save(reading)).thenAnswer(invocation -> {
            SensorReading r = invocation.getArgument(0);
            r.setTimestamp(LocalDateTime.now());
            return r;
        });
        SensorReading saved = sensorService.submitReading(field.getId(), reading);
        assertNotNull(saved);
        assertNotNull(saved.getTimestamp());
        assertTrue(saved.getValue() < field.getMoistureThreshold());
        verify(readingRepo).save(reading);

       
    }



    @Test
    void testReadingSavedWithTimestamp() {
        SensorReading reading = new SensorReading();
        reading.setSensorType("temperature");
        reading.setValue(40.0);

        when(readingRepo.save(reading)).thenAnswer(invocation -> {
            SensorReading r = invocation.getArgument(0);
            r.setTimestamp(LocalDateTime.now());
            return r;
        });

        SensorReading saved = sensorService.submitReading(field.getId(), reading);
        assertNotNull(saved.getTimestamp());
    }
}
