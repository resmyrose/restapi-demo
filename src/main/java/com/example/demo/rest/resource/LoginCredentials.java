package com.example.demo.rest.resource;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginCredentials {

	@NotEmpty
	private String username;
	
	@NotEmpty
	private String password;
}
