package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import com.mindex.challenge.service.ReportingStructureService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReportingStructureServiceImplTest {

    private String reportingIdUrl;
    private String employeeUrl;

    @Autowired
    private ReportingStructureService reportingStructureService;
    private EmployeeService employeeService;
    

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;
    
    @Before
    public void setup() {
        employeeUrl = "http://localhost:" + port + "/employee";
        reportingIdUrl = "http://localhost:" + port + "/reporting/{id}";
    }

    @Test
    public void testRead() {
        // create employees for testing
        Employee employee = new Employee();
        employee.setFirstName("John");
        employee.setLastName("Prime");
        employee.setDepartment("Engineering");
        employee.setPosition("Sr.Developer");

        Employee newEmployee = new Employee();
        newEmployee.setFirstName("John");
        newEmployee.setLastName("Doe");
        newEmployee.setDepartment("Engineering");
        newEmployee.setPosition("Developer");
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, newEmployee, Employee.class).getBody();
        assertNotNull(createdEmployee.getEmployeeId());

        ArrayList<Employee> reports = new ArrayList<Employee>();
        reports.add(createdEmployee);
        employee.setDirectReports(reports);

        Employee createdBoss = restTemplate.postForEntity(employeeUrl, employee, Employee.class).getBody();
        assertNotNull(createdBoss.getEmployeeId());

        
        // employee with one report
        ReportingStructure structure = new ReportingStructure();
        structure.setEmployee(createdBoss);
        structure.setNumberOfReports(1);
        ReportingStructure oneReportReadStructure = restTemplate.getForEntity(reportingIdUrl, ReportingStructure.class, createdBoss.getEmployeeId()).getBody();
        assertReportingStructureEquivalence(structure, oneReportReadStructure);

        // employee with no reports
        structure.setEmployee(createdEmployee);
        structure.setNumberOfReports(0);
        ReportingStructure noReportReadStructure = restTemplate.getForEntity(reportingIdUrl, ReportingStructure.class, createdEmployee.getEmployeeId()).getBody();
        assertReportingStructureEquivalence(structure, noReportReadStructure);

    }

    private static void assertReportingStructureEquivalence(ReportingStructure expected, ReportingStructure actual) {
        assertEquals(expected.getEmployee().getFirstName(), actual.getEmployee().getFirstName());
        assertEquals(expected.getEmployee().getLastName(), actual.getEmployee().getLastName());
        assertEquals(expected.getEmployee().getDepartment(), actual.getEmployee().getDepartment());
        assertEquals(expected.getEmployee().getPosition(), actual.getEmployee().getPosition());
        assertEquals(expected.getNumberOfReports(), actual.getNumberOfReports());
    }
}
