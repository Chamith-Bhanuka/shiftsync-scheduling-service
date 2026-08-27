package com.shiftsync.scheduling_service.controller;

import com.shiftsync.scheduling_service.client.NotificationClient;
import com.shiftsync.scheduling_service.dto.SwapRequestCreateRequest;
import com.shiftsync.scheduling_service.dto.SwapRequestResponse;
import com.shiftsync.scheduling_service.dto.SwapResponseRequest;
import com.shiftsync.scheduling_service.entities.Employee;
import com.shiftsync.scheduling_service.entities.Shift;
import com.shiftsync.scheduling_service.entities.SwapRequest;
import com.shiftsync.scheduling_service.repository.EmployeeRepository;
import com.shiftsync.scheduling_service.repository.ShiftRepository;
import com.shiftsync.scheduling_service.repository.SwapRequestRepository;
import com.shiftsync.scheduling_service.service.SwapService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/swap-requests")
public class SwapController {
    private final SwapService swapService;
    private final SwapRequestRepository swapRequestRepository;
    private final ShiftRepository shiftRepository;
    private final EmployeeRepository employeeRepository;
    private final NotificationClient notificationClient;

    public SwapController(SwapService swapService, SwapRequestRepository swapRequestRepository,
                          ShiftRepository shiftRepository, EmployeeRepository employeeRepository,
                          NotificationClient notificationClient) {
        this.swapService = swapService;
        this.swapRequestRepository = swapRequestRepository;
        this.shiftRepository = shiftRepository;
        this.employeeRepository = employeeRepository;
        this.notificationClient = notificationClient;
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

        Employee targetEmployee = null;
        if (request.getTargetEmployeeId() != null) {
            targetEmployee = employeeRepository.findById(request.getTargetEmployeeId())
                    .orElseThrow(() -> new RuntimeException("Employee not found: " + request.getTargetEmployeeId()));
            swapRequest.setTargetEmployee(targetEmployee);
        }

        swapRequest.setStatus(SwapRequest.Status.PENDING);
        swapRequest.setCreatedAt(LocalDateTime.now());
        SwapRequest saved = swapRequestRepository.save(swapRequest);

        notificationClient.logSwapCreated(
                saved.getId(), shift.getId(),
                requestingEmployee.getId(), requestingEmployee.getName(),
                targetEmployee != null ? targetEmployee.getId() : null,
                targetEmployee != null ? targetEmployee.getName() : null
        );

        return saved;
    }

    @PutMapping("/{id}/approve")
    public SwapRequest approve(@PathVariable Long id) {
        return swapService.approve(id);
    }

    @PutMapping("/{id}/reject")
    public SwapRequest reject(@PathVariable Long id) {
        return swapService.reject(id);
    }

    @PutMapping("/{id}/respond")
    public SwapRequest respond(@PathVariable Long id, @RequestBody SwapResponseRequest response) {
        return swapService.respond(id, response);
    }

    @GetMapping("/awaiting-decision")
    public List<SwapRequestResponse> getAwaitingDecision() {
        List<SwapRequest> responded = swapRequestRepository.findByStatus(SwapRequest.Status.RESPONDED);
        List<SwapRequest> pending = swapRequestRepository.findByStatus(SwapRequest.Status.PENDING);
        List<SwapRequest> all = new java.util.ArrayList<>();
        if (responded != null) all.addAll(responded);
        if (pending != null) all.addAll(pending);
        return all.stream().map(SwapRequestResponse::from).collect(Collectors.toList());
    }

    @GetMapping
    public List<SwapRequestResponse> getSwapRequests(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long employeeId) {

        List<SwapRequest> results;
        if (status != null) {
            results = swapRequestRepository.findByStatus(SwapRequest.Status.valueOf(status.toUpperCase()));
        } else if (employeeId != null) {
            results = swapRequestRepository.findByRequestingEmployeeIdOrTargetEmployeeId(employeeId, employeeId);
        } else {
            results = swapRequestRepository.findAll();
        }
        return results.stream().map(SwapRequestResponse::from).collect(Collectors.toList());
    }
}
