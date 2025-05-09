package com.ncu.SmartFarming_Montioring_System.Repositery;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ncu.SmartFarming_Montioring_System.Entity.Field;
@Repository
public interface SmartFarmingRepositery extends JpaRepository<Field, Long> {

}

