package com.ncu.SmartFarming_Montioring_System.Aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ncu.SmartFarming_Montioring_System.Entity.SensorReading;
@Aspect
@Component
public class SensorLogging {
	private static final Logger logger = LoggerFactory.getLogger(SensorLogging.class);

    @AfterReturning(
        pointcut = "execution(* com.ncu.SmartFarming_Montioring_System.Repositery.SensorReadingRepositery.save(..))",
        returning = "reading")
    public void logSensorReadingSaved(SensorReading reading) {
        if (reading != null) {
            logger.info("Sensor reading recorded -> Field ID: {}, Type: {}, Value: {}",
                    reading.getField().getId(),
                    reading.getSensorType(),
                    reading.getValue());}
        }
    
    @Around("execution(* com.ncu.SmartFarming_Montioring_System.Repositery.SensorReadingRepositery.save(..))")
    public Object aroundSensorReading(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        SensorReading sensorReading = (SensorReading) args[0];
        logger.info("Attempting to record sensor reading: Field ID: {}, Sensor Type: {}, Value: {}",
                sensorReading.getField().getId(),
                sensorReading.getSensorType(),
                sensorReading.getValue());

        
        Object result = joinPoint.proceed();

      
        if ("temperature".equalsIgnoreCase(sensorReading.getSensorType())) {
            Double threshold = sensorReading.getField().getMoistureThreshold();
            if (threshold != null && sensorReading.getValue() < threshold) {
                logger.warn("⚠️ ALERT: Field {} moisture is low! Value: {} < Threshold: {}",
                        sensorReading.getField().getId(),
                        sensorReading.getValue(),
                        threshold);
            }
        }

        return result;
    }
    @AfterThrowing(
    	    pointcut = "execution(* com.ncu.SmartFarming_Montioring_System.Repositery.SensorReadingRepositery.save(..))",
    	    throwing = "ex")
    	public void logSensorReadingException(Exception ex) {
    	    logger.error("❌ Exception while recording sensor reading: {}", ex.getMessage(), ex);
    	}
}


