package com.shiftsync.scheduling_service.controller;

import com.shiftsync.scheduling_service.dto.EmployeeRequest;
import com.shiftsync.scheduling_service.entities.Employee;
import com.shiftsync.scheduling_service.entities.Location;
import com.shiftsync.scheduling_service.repository.EmployeeRepository;
import com.shiftsync.scheduling_service.repository.LocationRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {
    private final EmployeeRepository employeeRepository;
    private final LocationRepository locationRepository;

    public EmployeeController(EmployeeRepository employeeRepository, LocationRepository locationRepository) {
        this.employeeRepository = employeeRepository;
        this.locationRepository = locationRepository;
    }

    @PostMapping
    public Employee create(@RequestBody EmployeeRequest request) {
        Location location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new RuntimeException("Location not found: " + request.getLocationId()));
        Employee employee = new Employee();
        employee.setLocation(location);
        employee.setName(request.getName());
        employee.setEmail(request.getEmail());
        employee.setRole(request.getRole());
        return employeeRepository.save(employee);
    }

    @GetMapping
    public List<Employee> getByLocation(@RequestParam Long locationId) {
        return employeeRepository.findByLocationId(locationId);
    }
}
