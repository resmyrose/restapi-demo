package com.example.demo.rest.resource;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Patient {

	private String id;
	
	@NotEmpty
	private String firstName;
	
	@NotEmpty
	private String lastName;
	
	private String dieticianId;
	
	@JsonIgnore
	private String dieticianEmail;
	
	@NotEmpty
	@Pattern(regexp = "\\d{3}-\\d{3}-\\d{4}")
	private String contactNumber;
	
	@NotEmpty
	@Email
	private String email;
	
	private String password;
	
	private List<String> allergies;
	
	private String foodPreference;
	
	private String cuisineCategory;
	
	private PatientVitals vitals;
	
}
