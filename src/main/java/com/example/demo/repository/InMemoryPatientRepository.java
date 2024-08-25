package com.example.demo.repository;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Component;

import com.example.demo.rest.resource.Patient;

@Component
public class InMemoryPatientRepository implements PatientRepository {

	ConcurrentMap<String, Patient> patientStore = new ConcurrentHashMap<>();
	
	@Override
	public Patient getPatientById(String patientId) {
		return patientStore.get(patientId);
	}

	@Override
	public Collection<Patient> getPatientsByDieticianId(String dieticianId) {
		return patientStore.values().stream()
		.filter(p -> p.getDieticianId().equals(dieticianId))
		.toList();
	}

	@Override
	public Collection<Patient> getPatients() {
		return patientStore.values();
	}

	@Override
	public Patient savePatient(Patient patient) {
		patientStore.put(patient.getId(), patient);
		return patient;
	}

	@Override
	public void deletePatient(String patientId) {
		patientStore.remove(patientId);
	}

	@Override
	public Optional<Patient> getPatientByEmail(String email) {
		return patientStore.values().stream()
				.filter(d -> d.getEmail().equals(email))
				.findAny();
	}

}
