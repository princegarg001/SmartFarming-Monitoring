package com.ncu.SmartFarming_Montioring_System.Service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ncu.SmartFarming_Montioring_System.Entity.Field;
import com.ncu.SmartFarming_Montioring_System.Exception.FieldNotFoundException;
import com.ncu.SmartFarming_Montioring_System.Repositery.SmartFarmingRepositery;
@Service
public class FeildServiceimpl implements FieldService {
@Autowired
SmartFarmingRepositery fieldRepo ;
   @Override
	public Field registerField(Field field) {
		return fieldRepo.save(field);
		
	}

	@Override
	public Field updateField(Long id, Field updatedField) {
		
		return fieldRepo.findById(id)
                .map(field -> {
                    field.setName(updatedField.getName());
                    field.setLocation(updatedField.getLocation());
                    field.setCropType(updatedField.getCropType());
                    field.setMoistureThreshold(updatedField.getMoistureThreshold());
                    return fieldRepo.save(field);
                })
                .orElseThrow(() -> new FieldNotFoundException("Field with ID " + id + " not found"));
    }

	@Override
	public Optional<Field> findById(Long id) {
		Field field = fieldRepo.findById(id)
		        .orElseThrow(() -> new FieldNotFoundException("Field not found"));

		    return Optional.of(field);
	}

	

}
