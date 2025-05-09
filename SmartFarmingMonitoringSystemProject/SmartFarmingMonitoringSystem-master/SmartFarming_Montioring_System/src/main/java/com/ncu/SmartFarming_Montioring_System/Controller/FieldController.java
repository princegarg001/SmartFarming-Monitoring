package com.ncu.SmartFarming_Montioring_System.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.ncu.SmartFarming_Montioring_System.Entity.Field;
//import com.ncu.SmartFarming_Montioring_System.Entity.SensorReading;
import com.ncu.SmartFarming_Montioring_System.Security.User;
import com.ncu.SmartFarming_Montioring_System.Security.UserRepository;
import com.ncu.SmartFarming_Montioring_System.Service.FieldService;
import com.ncu.SmartFarming_Montioring_System.Service.SensorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Field Management", description = "Endpoints for registering and updating fields")
@RestController
@RequestMapping("/Fields")
public class FieldController {

    @Autowired
    private FieldService fieldService;
    @Autowired
    private SensorService sensorService;
    @Autowired
    private UserRepository userRepository;
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Register a new field")
    @PostMapping("/add")
    public ResponseEntity<Field> registerField(@RequestBody Field field) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        // Get the user from the repository
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        // Set the owner of the field
        field.setOwner(user);

        return ResponseEntity.status(201).body(fieldService.registerField(field));
    }
    @PreAuthorize("hasAnyAuthority('FARMER', 'ADMIN')")
    @Operation(summary = "Update an existing field")
    @PutMapping("/{id}")
    public ResponseEntity<Field> updateField(@PathVariable Long id, @RequestBody Field updatedField) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        boolean isAdmin = auth.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"));

        // Get the existing field
        Field existingField = fieldService.findById(id)
                .orElse(null);

        if (existingField == null) {
            return ResponseEntity.notFound().build();
        }

        if (isAdmin) {
            // Preserve the owner
            updatedField.setOwner(existingField.getOwner());
            Field updated = fieldService.updateField(id, updatedField);
            return ResponseEntity.ok(updated);
        }

        // If user is FARMER, check if they own the field
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        if (existingField.getOwner() != null && existingField.getOwner().getId().equals(user.getId())) {
            // Preserve the owner
            updatedField.setOwner(existingField.getOwner());
            Field updated = fieldService.updateField(id, updatedField);
            return ResponseEntity.ok(updated);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to update this field");
        }
    }
    @PreAuthorize("hasAnyAuthority('FARMER', 'ADMIN')")
    @Operation(summary = "Get a field by ID (access restricted by role)")
    @GetMapping("/{id}")
    public ResponseEntity<Field> getFieldById(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        boolean isAdmin = auth.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"));

        // Get the field
        Field field = fieldService.findById(id)
                .orElse(null);

        if (field == null) {
            return ResponseEntity.notFound().build();
        }

        // If user is ADMIN, allow access to any field
        if (isAdmin) {
            return ResponseEntity.ok(field);
        }

        // If user is FARMER, check if they own the field
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        if (field.getOwner() != null && field.getOwner().getId().equals(user.getId())) {
            return ResponseEntity.ok(field);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to access this field");
        }
    }
}
