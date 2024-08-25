package com.example.demo.repository;

import java.util.Collection;
import java.util.Optional;

import com.example.demo.rest.resource.Patient;

public interface PatientRepository {

	Patient getPatientById(String patientId);
	Optional<Patient> getPatientByEmail(String email);
	Collection<Patient> getPatientsByDieticianId(String dieticianId);
	Collection<Patient> getPatients();
	Patient savePatient(Patient patient);
    void deletePatient(String patientId);

}
