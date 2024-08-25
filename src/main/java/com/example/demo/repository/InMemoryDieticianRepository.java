package com.example.demo.repository;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Component;

import com.example.demo.rest.resource.Dietician;

@Component
public class InMemoryDieticianRepository implements DieticianRepository {
	ConcurrentMap<String, Dietician> dieticianStore = new ConcurrentHashMap<>();

	@Override
	public Dietician getDieticianById(String dieticianId) {
		return dieticianStore.get(dieticianId);
	}

	@Override
	public Collection<Dietician> getDieticians() {
		return dieticianStore.values();
	}

	@Override
	public Dietician saveDietician(Dietician dietician) {
		dieticianStore.put(dietician.getId(), dietician);
		return dietician;
	}

	@Override
	public void deleteDietician(String dieticianId) {
		dieticianStore.remove(dieticianId);

	}

	@Override
	public Optional<Dietician> getDieticianByEmail(String email) {
		return dieticianStore.values().stream()
		.filter(d -> d.getEmail().equals(email))
		.findAny();
	}

}
