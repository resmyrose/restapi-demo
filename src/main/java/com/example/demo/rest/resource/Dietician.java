package com.example.demo.rest.resource;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Dietician {

	private String id;
	
	@NotEmpty
	private String firstName;
	
	@NotEmpty
	private String lastName;
	
	@NotEmpty
	@Pattern(regexp = "\\d{3}-\\d{3}-\\d{4}")
	private String contactNumber;
	
	@Pattern(regexp = "\\d{2}/\\d{2}/\\d{4}")
	private String dateOfBirth;
	
	@NotEmpty
	@Email
	private String email;
	
	private String password;
	
	private String hospitalName;
	
	private String hospitalStreet;
	
	private String hospitalCity;
	
	private String hospitalZipCode;
	
	private String education;
}
