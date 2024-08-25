package com.example.demo.repository;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Component;

import com.example.demo.rest.resource.TestReport;

@Component
public class InMemoryTestReportRepository implements TestReportRepository {

	ConcurrentMap<String, TestReport> testReportStore = new ConcurrentHashMap<>();
	
	@Override
	public TestReport getTestReportById(String reportId) {
		return testReportStore.get(reportId);
	}

	@Override
	public Collection<TestReport> getTestReportsByPatientId(String patientId) {
		return testReportStore.values().stream()
				.filter(r -> r.getPatientId().equals(patientId))
				.toList();
	}

	@Override
	public Collection<TestReport> getTestReportsByTestType(String testType) {
		return testReportStore.values().stream()
				.filter(r -> r.getTestType().equals(testType))
				.toList();
	}

	@Override
	public Collection<TestReport> getTestReportsByMorbidityName(String morbidity) {
		return testReportStore.values().stream()
				.filter(r -> r.getMorbidity().equals(morbidity))
				.toList();
	}

	@Override
	public TestReport saveTestReport(TestReport testReport) {
		testReportStore.put(testReport.getId(), testReport);
		return testReport;
	}

	@Override
	public void deleteTestReport(String reportId) {
		testReportStore.remove(reportId);
	}

}
