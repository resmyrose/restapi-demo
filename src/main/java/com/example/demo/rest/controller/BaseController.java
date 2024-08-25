package com.example.demo.rest.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.example.demo.repository.DieticianRepository;
import com.example.demo.repository.PatientRepository;

import jakarta.validation.ConstraintViolationException;

public abstract class BaseController {

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Map<String, String> handleValidationExceptions(
	  MethodArgumentNotValidException ex) {
	    Map<String, String> errors = new HashMap<>();
	    ex.getBindingResult().getAllErrors().forEach((error) -> {
	        String fieldName = ((FieldError) error).getField();
	        String errorMessage = error.getDefaultMessage();
	        errors.put(fieldName, errorMessage);
	    });
	    return errors;
	}
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(ConstraintViolationException.class)
	public Map<String, String> handleConstraintViolationExceptions(
			ConstraintViolationException ex) {
	    Map<String, String> errors = new HashMap<>();
	    ex.getConstraintViolations().forEach((error) -> {
	        String fieldName = error.getPropertyPath().toString();
	        String errorMessage = error.getMessage();
	        errors.put(fieldName, errorMessage);
	    });
	    return errors;
	}
	
	protected Map<String, String> buildErrorResponse(String message) {
		Map<String, String> response = new HashMap<>();
		response.put("message", message);
		return response;
	}
	
	protected boolean userNameAlreadyExists(String userName, DieticianRepository dieticianRepository, PatientRepository patientRepository) {
		if (dieticianRepository.getDieticianByEmail(userName).isPresent()) {
			return true;
		}
		if (patientRepository.getPatientByEmail(userName).isPresent()) {
			return true;
		}
		return false;
	}
}
