package com.example.demo.rest.controller;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.repository.DieticianRepository;
import com.example.demo.repository.PatientRepository;
import com.example.demo.rest.exception.RestException;
import com.example.demo.rest.resource.Dietician;
import com.example.demo.rest.resource.LoginCredentials;
import com.example.demo.rest.resource.Patient;
import com.example.demo.rest.util.AuthUtil;

import jakarta.validation.Valid;

@RestController
public class LoginController {
	
	@Value("${auth.admin.username}")
	private String adminUsername;
	
	@Value("${auth.admin.password}")
	private String adminPassword;
	
	@Value("${auth.jwt.private.key}")
	private String jwtPrivateKey;
	
	@Autowired
	private DieticianRepository dieticianRepository;
	
	@Autowired
	private PatientRepository patientRepository;
	

	@PostMapping(value = "/auth/login", consumes = MediaType.APPLICATION_JSON_VALUE,
			   produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map> authenticate(@RequestBody @Valid LoginCredentials credentials) {
		try {
			if (isAdminCredentials(credentials)) {
				String token = processAdminLogin(credentials);
				System.out.println("generated token for admin : "+token);
				return new ResponseEntity<Map>(Map.of("token", token), HttpStatusCode.valueOf(200));
			}
			
			// check dietician repository for matching email
			Optional<Dietician> matchingDietician = dieticianRepository.getDieticianByEmail(credentials.getUsername());
			if (matchingDietician.isPresent()) {
				String token = processDieticianLogin(credentials, matchingDietician.get());
				System.out.println("generated token for dietician : "+token);
				return new ResponseEntity<Map>(Map.of("token", token), HttpStatusCode.valueOf(200));
			}
			
			// check patient repository for matching email
			Optional<Patient> matchingPatient = patientRepository.getPatientByEmail(credentials.getUsername());
			if (matchingPatient.isPresent()) {
				String token = processPatientLogin(credentials, matchingPatient.get());
				System.out.println("generated token for patient : "+token);
				return new ResponseEntity<Map>(Map.of("token", token), HttpStatusCode.valueOf(200));
			}

		} catch (RestException re) {
			return new ResponseEntity<Map>(Map.of("error", re.getMessage()), HttpStatusCode.valueOf(400));
		}
		return new ResponseEntity<Map>(Map.of("error", "login failed"), HttpStatusCode.valueOf(500));
	}

	private String processPatientLogin(LoginCredentials credentials, Patient patient) throws RestException {
		if (credentials.getPassword().equals(patient.getPassword())) {
			try {
				System.out.println("patient creds valid. generating token...");
				return AuthUtil.generateAccessToken(credentials.getUsername(), List.of("patient"), jwtPrivateKey);
			} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
				throw new RuntimeException(e);
			}
		} else {
			throw new RestException("login failed");
		}
	}

	private String processDieticianLogin(LoginCredentials credentials, Dietician dietician) throws RestException {
		if (credentials.getPassword().equals(dietician.getPassword())) {
			try {
				System.out.println("dietician creds valid. generating token...");
				return AuthUtil.generateAccessToken(credentials.getUsername(), List.of("dietician"), jwtPrivateKey);
			} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
				throw new RuntimeException(e);
			}
		} else {
			throw new RestException("login failed");
		}
	}

	private String processAdminLogin(LoginCredentials credentials) throws RestException {
		if (credentials.getPassword().equals(adminPassword)) {
			try {
				System.out.println("admin creds valid. generating token...");
				return AuthUtil.generateAccessToken(adminUsername, List.of("admin"), jwtPrivateKey);
			} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
				throw new RuntimeException(e);
			}
		} else {
			throw new RestException("login failed");
		}
	}

	private boolean isAdminCredentials(LoginCredentials credentials) {
		return credentials.getUsername().equals(adminUsername);
	}
}
