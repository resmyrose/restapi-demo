package com.example.demo.rest.resource;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatientVitals {
	
	@NotNull
	private float weight;
	
	@NotNull
	private float height;
	
	@NotNull
	private float temperature;
	
	@NotNull
	private int systolicBP;
	
	@NotNull
	private int diastolicBP;
}
