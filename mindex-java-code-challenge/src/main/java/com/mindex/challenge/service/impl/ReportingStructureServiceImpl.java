package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.ReportingStructureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

@Service
public class ReportingStructureServiceImpl implements ReportingStructureService {

    private static final Logger LOG = LoggerFactory.getLogger(ReportingStructureServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public ReportingStructure read(String id) {
        LOG.debug("Getting all reports for employee with id [{}]", id);

        Employee employee = employeeRepository.findByEmployeeId(id);
        ReportingStructure structure = new ReportingStructure();
        structure.setEmployee(employee);
        structure.setNumberOfReports(0);
        

        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        return getAllReports(employee, structure);

    }

    /**
     * Recursively runs through all direct reports and updates reporting structure 
     * @param employee
     * @param reports
     * @param structure 
     * @return Reporting structure with fully fleshout employee and number of reports
     */
    private ReportingStructure getAllReports(Employee employee, ReportingStructure structure){
        if(employee.getDirectReports() == null){
            return structure;
        }else{
            ArrayList<Employee> directReports = new ArrayList<Employee>();
            for(Employee e: employee.getDirectReports()){
                Employee emp = employeeRepository.findByEmployeeId(e.getEmployeeId());
                directReports.add(emp);
                structure.setNumberOfReports(structure.getNumberOfReports()+1);
                getAllReports(emp, structure);
            }
            employee.setDirectReports(directReports);
        }
        return structure;
    }
}