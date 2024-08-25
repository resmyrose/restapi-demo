package com.example.demo.rest.resource;

import java.util.Date;
import java.util.Map;

import com.example.demo.rest.constants.TestType;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotNull;

import com.example.demo.rest.constants.Morbidity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestReport {

	private String id;
	
	private String patientId;
	
	@NotNull
	private String testType;
	
	@NotNull
	private String morbidity;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
	private Date testDate;
	
	private String location;
	
	@NotNull
	private Map<String,Float> readings;
	
}
