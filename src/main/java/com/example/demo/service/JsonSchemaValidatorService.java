package com.example.demo.service;

import java.util.Set;

import org.springframework.stereotype.Service;

import com.example.demo.rest.resource.Dietician;
import com.example.demo.rest.resource.Patient;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

@Service
public class JsonSchemaValidatorService {

	private final Validator validator;
	
	JsonSchemaValidatorService(Validator validator) {
        this.validator = validator;
    }
 
    public void validateDietician(Dietician dietician) {
        Set<ConstraintViolation<Dietician>> violations = validator.validate(dietician);
 
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<Dietician> constraintViolation : violations) {
                sb.append(constraintViolation.getMessage());
            }
            throw new ConstraintViolationException("Error occurred: " + sb, violations);
        }
    }
    
    public void validatePatient(Patient patient) {
        Set<ConstraintViolation<Patient>> violations = validator.validate(patient);
 
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<Patient> constraintViolation : violations) {
                sb.append(constraintViolation.getMessage());
            }
            throw new ConstraintViolationException("Error occurred: " + sb, violations);
        }
    }
}
