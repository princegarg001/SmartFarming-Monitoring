package com.ncu.SmartFarming_Montioring_System;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.ncu.SmartFarming_Montioring_System.Entity.Field;
import com.ncu.SmartFarming_Montioring_System.Repositery.SmartFarmingRepositery;

@SpringBootApplication
public class SmartFarmingMontioringSystemApplication implements CommandLineRunner {
	private static final Logger log = LoggerFactory.getLogger(SmartFarmingMontioringSystemApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SmartFarmingMontioringSystemApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		log.info("🚀 IoT Monitoring Service started .");
    }
		
	}



