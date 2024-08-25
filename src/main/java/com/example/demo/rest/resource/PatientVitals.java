package com.example.demo.rest.resource;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatientVitals {
	private float weight;
	private float height;
	private float temperature;
	private int systolicBP;
	private int diastolicBP;
}
