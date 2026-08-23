package com.shiftsync.scheduling_service.service;

import com.shiftsync.scheduling_service.entities.Employee;
import com.shiftsync.scheduling_service.entities.Shift;
import com.shiftsync.scheduling_service.repository.EmployeeRepository;
import com.shiftsync.scheduling_service.repository.ShiftRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AvailabilityService {
    private final ShiftRepository shiftRepository;
    private final EmployeeRepository employeeRepository;

    public AvailabilityService(ShiftRepository shiftRepository, EmployeeRepository employeeRepository) {
        this.shiftRepository = shiftRepository;
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> getAvailableEmployees(Shift targetShift) {
        Long locationId = targetShift.getLocation().getId();
        LocalDateTime start = targetShift.getStartTime();
        LocalDateTime end = targetShift.getEndTime();

        List<Employee> allEmployees = employeeRepository.findByLocationId(locationId);
        List<Shift> allAssignedShifts = shiftRepository.findByLocationIdAndEmployeeIsNotNull(locationId);

        return allEmployees.stream()
                .filter(employee -> !hasConflict(employee, targetShift, start, end, allAssignedShifts))
                .collect(Collectors.toList());
    }

    private boolean hasConflict(Employee employee, Shift targetShift, LocalDateTime start, LocalDateTime end,
                                List<Shift> allAssignedShifts) {
        return allAssignedShifts.stream()
                .filter(s -> !s.getId().equals(targetShift.getId())) // ignore the shift being swapped itself
                .filter(s -> s.getEmployee().getId().equals(employee.getId()))
                .anyMatch(s -> overlaps(s.getStartTime(), s.getEndTime(), start, end));
    }

    // Standard interval overlap check: two ranges overlap if one starts before the other ends, both ways
    private boolean overlaps(LocalDateTime aStart, LocalDateTime aEnd, LocalDateTime bStart, LocalDateTime bEnd) {
        return aStart.isBefore(bEnd) && bStart.isBefore(aEnd);
    }
}
