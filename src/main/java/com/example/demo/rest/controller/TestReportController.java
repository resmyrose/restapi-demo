package com.example.demo.rest.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.repository.PatientRepository;
import com.example.demo.repository.TestReportRepository;
import com.example.demo.rest.constants.Morbidity;
import com.example.demo.rest.constants.TestType;
import com.example.demo.rest.resource.Patient;
import com.example.demo.rest.resource.TestReport;
import com.example.demo.rest.util.AuthUtil;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
public class TestReportController extends BaseController {

	@Value("${patient.max.reports}")
	private int maxReportsPerPatient;
	
	@Autowired
	private PatientRepository patientRepository;
	
	@Autowired
	private TestReportRepository testReportRepository;
	
	private static final Set<String> ALLOWED_MORBIDITY_VALUES = Set.of("DIABETES", "OBESITY", "ANEMIA", "FOOD_ALLERGY");
	private static final Set<String> ALLOWED_TEST_TYPE_VALUES = Set.of("BLOOD_DRAW", "URINE_SAMPLE", "X_RAY", "ULTRASOUND","VISION", "HEARING", "PHYSICAL", "STRESS", "MRI");
	
	@PostMapping(value = "/v1/patients/{patient_id}/test-reports", consumes = MediaType.APPLICATION_JSON_VALUE,
			   produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<? extends Object> submitTestReportForPatient(@PathVariable("patient_id") String patientId,
			HttpServletRequest request,
			@RequestBody @Valid TestReport testReport) {
		Patient patient = patientRepository.getPatientById(patientId);
		if (patient == null) {
			return new ResponseEntity<TestReport>(HttpStatusCode.valueOf(404));
		} else if (!AuthUtil.hasPatientAccess(request, patient)) {
			return new ResponseEntity<TestReport>(HttpStatusCode.valueOf(403));
		}
		// check max test reports limit reached for patient
		Collection<TestReport> testReports = testReportRepository.getTestReportsByPatientId(patientId);
		if (testReports != null && testReports.size() >= maxReportsPerPatient) {
			return new ResponseEntity<TestReport>(HttpStatusCode.valueOf(409));
		}
		
		if (!ALLOWED_MORBIDITY_VALUES.contains(testReport.getMorbidity())) {
			return ResponseEntity.badRequest().body(buildErrorResponse("invalid morbidity value. Allowed values are "+ALLOWED_MORBIDITY_VALUES));
		}
		if (!ALLOWED_TEST_TYPE_VALUES.contains(testReport.getTestType())) {
			return ResponseEntity.badRequest().body(buildErrorResponse("invalid testType value. Allowed values are "+ALLOWED_TEST_TYPE_VALUES));
		}

		return new ResponseEntity<TestReport>(addNewTestReport(testReport, patientId), HttpStatusCode.valueOf(201));
	}
	
	@GetMapping(value = "/v1/patients/{patient_id}/test-reports",
			   produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<? extends Object> getTestReportsForPatient(@PathVariable("patient_id") String patientId,
			HttpServletRequest request) {
		Patient patient = patientRepository.getPatientById(patientId);
		if (patient == null) {
			return new ResponseEntity<TestReport>(HttpStatusCode.valueOf(404));
		} else if (!AuthUtil.hasPatientAccess(request, patient)) {
			return new ResponseEntity<TestReport>(HttpStatusCode.valueOf(403));
		}

		Collection<TestReport> testReports = testReportRepository.getTestReportsByPatientId(patientId);

		return new ResponseEntity<Collection<TestReport>>(new ArrayList<>(testReports), HttpStatusCode.valueOf(200));
	}
	
	@GetMapping(value = "/v1/test-reports",
			   produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<? extends Object> getAllTestReportsWithFilters(
			@RequestParam(name = "morbidityName", required = false) String morbidityName, 
			@RequestParam(name = "testType", required = false) String testTypeName,
			HttpServletRequest request) {
		if (!AuthUtil.hasAdminAccess(request)) {
			System.out.println("Not Admin. Returning 403.");
			return new ResponseEntity<TestReport>(HttpStatusCode.valueOf(403));
		}
		// exactly one query parameter should be passed
		if ((StringUtils.isEmpty(testTypeName) && StringUtils.isEmpty(morbidityName)) ||
				(!StringUtils.isEmpty(testTypeName) && !StringUtils.isEmpty(morbidityName))) {
			System.out.println("Exactly one of 'morbidityName' or 'testType' should be passed. Returning 400.");
			return ResponseEntity.badRequest().body(buildErrorResponse("Exactly one of 'morbidityName' or 'testType' should be passed."));
		}
		
		if (!StringUtils.isEmpty(testTypeName)) {
			if (!ALLOWED_TEST_TYPE_VALUES.contains(testTypeName)) {
				return ResponseEntity.badRequest().body(buildErrorResponse("invalid testType value. Allowed values are "+ALLOWED_TEST_TYPE_VALUES));
			}
			Collection<TestReport> testReports = testReportRepository.getTestReportsByTestType(testTypeName);
			
			return new ResponseEntity<Collection<TestReport>>(new ArrayList<>(testReports), HttpStatusCode.valueOf(200));
		} 

		if (!ALLOWED_MORBIDITY_VALUES.contains(morbidityName)) {
			return ResponseEntity.badRequest().body(buildErrorResponse("invalid morbidity value. Allowed values are "+ALLOWED_MORBIDITY_VALUES));
		}
		Collection<TestReport> testReports = testReportRepository.getTestReportsByMorbidityName(morbidityName);

		return new ResponseEntity<Collection<TestReport>>(new ArrayList<>(testReports), HttpStatusCode.valueOf(200));
	}
	
	@GetMapping(value = "/v1/test-reports/{report_id}",
			   produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<? extends Object> getTestReportById(@PathVariable("report_id") String reportId,
			HttpServletRequest request) {
		TestReport report = testReportRepository.getTestReportById(reportId);
		
		if (report == null) {
			return new ResponseEntity<TestReport>(HttpStatusCode.valueOf(404));
		}
		Patient patient = patientRepository.getPatientById(report.getPatientId());
		
		if (!AuthUtil.hasPatientAccess(request, patient)) {
			return new ResponseEntity<TestReport>(HttpStatusCode.valueOf(403));
		}

		return new ResponseEntity<TestReport>(report, HttpStatusCode.valueOf(200));
	}
	
	@DeleteMapping(value = "/v1/test-reports/{report_id}")
	public ResponseEntity<? extends Object> deleteTestReportById(@PathVariable("report_id") String reportId,
			HttpServletRequest request) {
		TestReport report = testReportRepository.getTestReportById(reportId);
		
		if (report == null) {
			return new ResponseEntity<TestReport>(HttpStatusCode.valueOf(404));
		}
		Patient patient = patientRepository.getPatientById(report.getPatientId());
		
		if (!AuthUtil.hasPatientAccess(request, patient)) {
			return new ResponseEntity<TestReport>(HttpStatusCode.valueOf(403));
		}

		testReportRepository.deleteTestReport(reportId);
		return ResponseEntity.noContent().build();
	}

	private TestReport addNewTestReport(TestReport testReport, String patientId) {
		testReport.setId(UUID.randomUUID().toString());
		testReport.setPatientId(patientId);
		testReport.setTestDate(new Date());
		testReportRepository.saveTestReport(testReport);
		return testReport;
	}
}
