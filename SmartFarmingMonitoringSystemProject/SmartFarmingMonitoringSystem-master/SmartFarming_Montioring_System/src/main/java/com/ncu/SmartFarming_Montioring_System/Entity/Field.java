package com.ncu.SmartFarming_Montioring_System.Entity;

import java.util.List;


//import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ncu.SmartFarming_Montioring_System.Security.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Field {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

    private String name;
    private String location;
    private String cropType;
    private Double moistureThreshold;

    @OneToMany(mappedBy = "field", cascade = CascadeType.ALL)
    private List<SensorReading> sensorReadings;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;


  public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getCropType() {
		return cropType;
	}

	public void setCropType(String cropType) {
		this.cropType = cropType;
	}

	public Double getMoistureThreshold() {
		return moistureThreshold;
	}

	public void setMoistureThreshold(Double moistureThreshold) {
		this.moistureThreshold = moistureThreshold;
	}

	public List<SensorReading> getSensorReadings() {
		return sensorReadings;
	}

	public void setSensorReadings(List<SensorReading> sensorReadings) {
		this.sensorReadings = sensorReadings;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

public Field() {}
}
