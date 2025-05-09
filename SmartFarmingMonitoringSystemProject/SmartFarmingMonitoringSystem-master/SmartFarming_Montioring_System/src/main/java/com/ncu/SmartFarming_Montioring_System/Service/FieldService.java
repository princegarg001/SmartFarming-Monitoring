package com.ncu.SmartFarming_Montioring_System.Service;

import java.util.Optional;

import com.ncu.SmartFarming_Montioring_System.Entity.Field;

public interface FieldService {
	Field registerField(Field field);
    Field updateField(Long id, Field updatedField);
    Optional<Field> findById(Long id);
    
}
