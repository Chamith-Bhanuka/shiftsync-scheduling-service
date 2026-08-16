package com.shiftsync.scheduling_service.controller;

import com.shiftsync.scheduling_service.dto.ShiftRequest;
import com.shiftsync.scheduling_service.dto.ShiftResponse;
import com.shiftsync.scheduling_service.entities.Employee;
import com.shiftsync.scheduling_service.entities.Location;
import com.shiftsync.scheduling_service.entities.Shift;
import com.shiftsync.scheduling_service.repository.EmployeeRepository;
import com.shiftsync.scheduling_service.repository.LocationRepository;
import com.shiftsync.scheduling_service.repository.ShiftRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/shifts")
public class ShiftController {
    private final ShiftRepository shiftRepository;
    private final LocationRepository locationRepository;
    private final EmployeeRepository employeeRepository;

    public ShiftController(ShiftRepository shiftRepository, LocationRepository locationRepository, EmployeeRepository employeeRepository) {
        this.shiftRepository = shiftRepository;
        this.locationRepository = locationRepository;
        this.employeeRepository = employeeRepository;
    }

    @PostMapping
    public ShiftResponse create(@RequestBody ShiftRequest request) {
        Location location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new RuntimeException("Location not found: " + request.getLocationId()));

        Shift shift = new Shift();
        shift.setLocation(location);
        shift.setStartTime(request.getStartTime());
        shift.setEndTime(request.getEndTime());

        if (request.getEmployeeId() != null) {
            Employee employee = employeeRepository.findById(request.getEmployeeId())
                    .orElseThrow(() -> new RuntimeException("Employee not found: " + request.getEmployeeId()));
            shift.setEmployee(employee);
            shift.setStatus(Shift.Status.SCHEDULED);
        } else {
            shift.setStatus(Shift.Status.OPEN);
        }

        Shift saved = shiftRepository.save(shift);
        return ShiftResponse.from(saved);
    }

    @GetMapping
    public List<ShiftResponse> getByLocation(@RequestParam Long locationId) {
        return shiftRepository.findByLocationId(locationId)
                .stream()
                .map(ShiftResponse::from)
                .collect(Collectors.toList());
    }
}
