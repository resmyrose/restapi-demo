package com.example.demo.repository;

import java.util.Collection;

import com.example.demo.rest.resource.TestReport;

public interface TestReportRepository {

	TestReport getTestReportById(String reportId);
	Collection<TestReport> getTestReportsByPatientId(String patientId);
	Collection<TestReport> getTestReportsByTestType(String testName);
	Collection<TestReport> getTestReportsByMorbidityName(String morbidityName);
	TestReport saveTestReport(TestReport testReport);
    void deleteTestReport(String reportId);

}
