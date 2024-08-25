package com.example.demo.rest.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.repository.DieticianRepository;
import com.example.demo.repository.PatientRepository;
import com.example.demo.rest.resource.Dietician;
import com.example.demo.rest.resource.Patient;
import com.example.demo.rest.util.AuthUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
public class PatientController extends BaseController {
	
	@Autowired
	private DieticianRepository dieticianRepository;
	
	@Autowired
	private PatientRepository patientRepository;

	@PostMapping(value = "/v1/dieticians/{dietician_id}/patients", consumes = MediaType.APPLICATION_JSON_VALUE,
			   produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<? extends Object> createPatientForDietician(@PathVariable("dietician_id") String dieticianId,
			HttpServletRequest request,
			@RequestBody @Valid Patient patient) {
		Dietician dietician = dieticianRepository.getDieticianById(dieticianId);
		if (dietician == null) {
			return new ResponseEntity<Patient>(HttpStatusCode.valueOf(404));
		} else if (!AuthUtil.hasDieticianAccess(request, dietician)) {
			return new ResponseEntity<Patient>(HttpStatusCode.valueOf(403));
		}
		if (userNameAlreadyExists(patient.getEmail(),dieticianRepository, patientRepository)) {
			return ResponseEntity.status(409).body(buildErrorResponse("email already in use."));
		}
		return new ResponseEntity<Patient>(addNewPatient(patient, dieticianId), HttpStatusCode.valueOf(201));
	}
	
	@GetMapping(value = "/v1/patients/{patient_id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Patient> getPatient(@PathVariable("patient_id") String patientId,
			HttpServletRequest request) {
		Patient patient = patientRepository.getPatientById(patientId);
		if (patient == null) {
			return new ResponseEntity<Patient>(HttpStatusCode.valueOf(404));
		} else if (!AuthUtil.hasPatientAccess(request, patient)) {
			return new ResponseEntity<Patient>(HttpStatusCode.valueOf(403));
		}
		return ResponseEntity.ok(patient);
	}
	
	@DeleteMapping(value = "/v1/patients/{patient_id}")
	public ResponseEntity<Patient> deletePatient(@PathVariable("patient_id") String patientId,
			HttpServletRequest request) {
		Patient patient = patientRepository.getPatientById(patientId);
		if (patient == null) {
			return new ResponseEntity<Patient>(HttpStatusCode.valueOf(404));
		} else if (!AuthUtil.hasPatientAccess(request, patient)) {
			return new ResponseEntity<Patient>(HttpStatusCode.valueOf(403));
		}
		patientRepository.deletePatient(patientId);
		return ResponseEntity.noContent().build();
	}
	
	@GetMapping(value = "/v1/dieticians/{dietician_id}/patients", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Collection<Patient>> getPatientsOfDietician(@PathVariable("dietician_id") String dieticianId,
			HttpServletRequest request) {
		Dietician dietician = dieticianRepository.getDieticianById(dieticianId);
		if (dietician == null) {
			return new ResponseEntity<Collection<Patient>>(HttpStatusCode.valueOf(404));
		} else if (!AuthUtil.hasDieticianAccess(request, dietician)) {
			return new ResponseEntity<Collection<Patient>>(HttpStatusCode.valueOf(403));
		}
		return ResponseEntity.ok(patientRepository.getPatientsByDieticianId(dieticianId));
	}
	
	@GetMapping(value = "/v1/patients")
	public ResponseEntity<Collection<Patient>> getAllPatients(HttpServletRequest request) {
		
		if (!AuthUtil.hasAdminAccess(request)) {
			return new ResponseEntity<Collection<Patient>>(HttpStatusCode.valueOf(403));
		}
		return ResponseEntity.ok(patientRepository.getPatients());
	}
	
	private Patient addNewPatient(Patient patient, String dieticianId) {
		patient.setId(UUID.randomUUID().toString());
		patient.setPassword(UUID.randomUUID().toString().replaceAll("-", ""));
		patient.setDieticianId(dieticianId);
		patientRepository.savePatient(patient);
		return patient;
	}
	
	private boolean dieticianExists(String dieticianId) {
		return dieticianRepository.getDieticianById(dieticianId) != null;
	}
}
