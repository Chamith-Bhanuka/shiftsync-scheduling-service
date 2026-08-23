package com.shiftsync.scheduling_service.dto;

import com.shiftsync.scheduling_service.entities.SwapRequest;
import lombok.Getter;

@Getter
public class SwapRequestResponse {
    private Long id;
    private Long shiftId;
    private String shiftStartTime;
    private String shiftEndTime;
    private Long requestingEmployeeId;
    private String requestingEmployeeName;
    private Long targetEmployeeId;
    private String targetEmployeeName;
    private String status;
    private String employeeResponse;

    public static SwapRequestResponse from(SwapRequest r) {
        SwapRequestResponse dto = new SwapRequestResponse();
        dto.id = r.getId();
        dto.shiftId = r.getShift().getId();
        dto.shiftStartTime = r.getShift().getStartTime().toString();
        dto.shiftEndTime = r.getShift().getEndTime().toString();
        dto.requestingEmployeeId = r.getRequestingEmployee().getId();
        dto.requestingEmployeeName = r.getRequestingEmployee().getName();
        if (r.getTargetEmployee() != null) {
            dto.targetEmployeeId = r.getTargetEmployee().getId();
            dto.targetEmployeeName = r.getTargetEmployee().getName();
        }
        dto.status = r.getStatus().name();
        dto.employeeResponse = r.getEmployeeResponse();
        return dto;
    }
}
