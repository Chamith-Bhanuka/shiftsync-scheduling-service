package com.shiftsync.scheduling_service.controller;

import com.shiftsync.scheduling_service.dto.SwapRequestCreateRequest;
import com.shiftsync.scheduling_service.entities.Employee;
import com.shiftsync.scheduling_service.entities.Shift;
import com.shiftsync.scheduling_service.entities.SwapRequest;
import com.shiftsync.scheduling_service.repository.EmployeeRepository;
import com.shiftsync.scheduling_service.repository.ShiftRepository;
import com.shiftsync.scheduling_service.repository.SwapRequestRepository;
import com.shiftsync.scheduling_service.service.SwapService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/swap-requests")
public class SwapController {
    private final SwapService swapService;
    private final SwapRequestRepository swapRequestRepository;
    private final ShiftRepository shiftRepository;
    private final EmployeeRepository employeeRepository;

    public SwapController(SwapService swapService, SwapRequestRepository swapRequestRepository,
                          ShiftRepository shiftRepository, EmployeeRepository employeeRepository) {
        this.swapService = swapService;
        this.swapRequestRepository = swapRequestRepository;
        this.shiftRepository = shiftRepository;
        this.employeeRepository = employeeRepository;
    }

    @PostMapping
    public SwapRequest create(@RequestBody SwapRequestCreateRequest request) {
        Shift shift = shiftRepository.findById(request.getShiftId())
                .orElseThrow(() -> new RuntimeException("Shift not found: " + request.getShiftId()));
        Employee requestingEmployee = employeeRepository.findById(request.getRequestingEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found: " + request.getRequestingEmployeeId()));

        SwapRequest swapRequest = new SwapRequest();
        swapRequest.setShift(shift);
        swapRequest.setRequestingEmployee(requestingEmployee);

        if (request.getTargetEmployeeId() != null) {
            Employee targetEmployee = employeeRepository.findById(request.getTargetEmployeeId())
                    .orElseThrow(() -> new RuntimeException("Employee not found: " + request.getTargetEmployeeId()));
            swapRequest.setTargetEmployee(targetEmployee);
        }

        swapRequest.setStatus(SwapRequest.Status.PENDING);
        swapRequest.setCreatedAt(LocalDateTime.now());
        return swapRequestRepository.save(swapRequest);
    }

    @PutMapping("/{id}/approve")
    public SwapRequest approve(@PathVariable Long id) {
        return swapService.approve(id);
    }

    @PutMapping("/{id}/reject")
    public SwapRequest reject(@PathVariable Long id) {
        return swapService.reject(id);
    }
}
