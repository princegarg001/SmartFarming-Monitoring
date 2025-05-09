package com.ncu.SmartFarming_Montioring_System.Controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.ncu.SmartFarming_Montioring_System.Entity.Field;
import com.ncu.SmartFarming_Montioring_System.Entity.SensorReading;
import com.ncu.SmartFarming_Montioring_System.Exception.FieldNotFoundException;
import com.ncu.SmartFarming_Montioring_System.Exception.InvalidReadingException;
import com.ncu.SmartFarming_Montioring_System.Security.User;
import com.ncu.SmartFarming_Montioring_System.Security.UserRepository;
import com.ncu.SmartFarming_Montioring_System.Service.FieldService;
import com.ncu.SmartFarming_Montioring_System.Service.SensorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
@Tag(name = "Sensor Reading APIs", description = "APIs for managing field sensor readings")
@RestController
@RequestMapping("/Reading")
public class SensorReadingontroller {
@Autowired
SensorService sensorService;
@Autowired
private FieldService fieldService;
@Autowired
private UserRepository userRepository;
@PreAuthorize("hasAnyAuthority('FARMER', 'ADMIN')")
@Operation(summary = "Submit a new sensor reading for a field")
@PostMapping("/{fieldId}/readings/add")
public ResponseEntity<SensorReading> submitReading(@PathVariable Long fieldId, @RequestBody SensorReading reading) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String username = auth.getName();
    boolean isAdmin = auth.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"));

    Field field = fieldService.findById(fieldId)
            .orElseThrow(() -> new FieldNotFoundException("Field ID " + fieldId + " not found"));

    if (!isAdmin) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        if (field.getOwner() == null || !field.getOwner().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to add readings to this field");
        }
    }

    if (reading.getSensorType() == null || reading.getValue() == null) {
        throw new InvalidReadingException("Sensor type or value is missing");
    }

    reading.setField(field);
    reading.setTimestamp(LocalDateTime.now());
    SensorReading saved = sensorService.submitReading(fieldId, reading);

    return new ResponseEntity<>(saved, HttpStatus.CREATED);
}
@PreAuthorize("hasAnyAuthority('FARMER', 'ADMIN')")
@Operation(summary = "Get reading per sensor type for a field")
@GetMapping("/{fieldId}/readings")
public ResponseEntity<List<SensorReading>> getAllReadingsForField(@PathVariable Long fieldId) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String username = auth.getName();
    boolean isAdmin = auth.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"));

    Field field = fieldService.findById(fieldId)
            .orElseThrow(() -> new FieldNotFoundException("Field ID " + fieldId + " not found"));

    // If user is not ADMIN, check if they own the field
    if (!isAdmin) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        if (field.getOwner() == null || !field.getOwner().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to access readings for this field");
        }
    }

    List<SensorReading> readings = sensorService.getAllReadingsForField(fieldId);
    return new ResponseEntity<>(readings, HttpStatus.OK);
}
@PreAuthorize("hasAnyAuthority('FARMER', 'ADMIN')")
@GetMapping("/{fieldId}/filter")
public ResponseEntity<List<SensorReading>> getReadingsByTypeAndRange(
		 @PathVariable Long fieldId,
	        @RequestParam(required = false) String type,
	        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
	        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String username = auth.getName();
    boolean isAdmin = auth.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"));

	Field field = fieldService.findById(fieldId)
	        .orElseThrow(() -> new FieldNotFoundException("Field ID " + fieldId + " not found"));

   
    if (!isAdmin) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        if (field.getOwner() == null || !field.getOwner().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to access readings for this field");
        }
    }

	if (type != null && start != null && end != null) {
	    List<SensorReading> filtered = sensorService.getReadingsByTypeAndRange(fieldId, type, start, end);
	    return new ResponseEntity<>(filtered, HttpStatus.OK);
	}

	List<SensorReading> readings = sensorService.getReadingsByTypeAndRange(fieldId, type, start, end);
	return new ResponseEntity<>(readings, HttpStatus.OK);
}

@PreAuthorize("hasAnyAuthority('FARMER', 'ADMIN')")
@GetMapping("{fieldId}/readings/latest")
public ResponseEntity<Map<String, SensorReading>> getLatestReadingsPerType(@PathVariable Long fieldId) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String username = auth.getName();
    boolean isAdmin = auth.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"));

    Field field = fieldService.findById(fieldId)
            .orElseThrow(() -> new FieldNotFoundException("Field ID " + fieldId + " not found"));

    // If user is not ADMIN, check if they own the field
    if (!isAdmin) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        if (field.getOwner() == null || !field.getOwner().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to access readings for this field");
        }
    }

    Map<String, SensorReading> latestReadings = sensorService.getLatestReadingsPerType(fieldId);
    return ResponseEntity.ok(latestReadings);
}
@PreAuthorize("hasAnyAuthority('FARMER', 'ADMIN')")
@Operation(summary = "Get fields needing attention (e.g., low moisture)")
@GetMapping("/needs-attention")
public ResponseEntity<List<Field>> getFieldsNeedingAttention() {
    List<Field> fields = sensorService.getFieldsNeedingAttention();
    return new ResponseEntity<>(fields, HttpStatus.OK);
}
@PreAuthorize("hasAuthority('ADMIN')")
@Operation(summary = "Get all sensor readings in the system (ADMIN only)")
@GetMapping("/all")
public ResponseEntity<List<SensorReading>> getAllReadings() {
    return ResponseEntity.ok(sensorService.getAllReadings());
}


}
