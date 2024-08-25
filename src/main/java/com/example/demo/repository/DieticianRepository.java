package com.example.demo.repository;

import java.util.Collection;
import java.util.Optional;

import com.example.demo.rest.resource.Dietician;

public interface DieticianRepository {

	Dietician getDieticianById(String dieticianId);
	Optional<Dietician> getDieticianByEmail(String email);
	Collection<Dietician> getDieticians();
	Dietician saveDietician(Dietician dietician);
    void deleteDietician(String dieticianId);
}
