package com.shiftsync.scheduling_service.dto;

import com.shiftsync.scheduling_service.entities.Shift;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ShiftResponse {
    private Long id;
    private Long locationId;
    private Long employeeId;
    private String employeeName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;

    public static ShiftResponse from(Shift shift) {
        ShiftResponse r = new ShiftResponse();
        r.id = shift.getId();
        r.locationId = shift.getLocation().getId();
        if (shift.getEmployee() != null) {
            r.employeeId = shift.getEmployee().getId();
            r.employeeName = shift.getEmployee().getName();
        }
        r.startTime = shift.getStartTime();
        r.endTime = shift.getEndTime();
        r.status = shift.getStatus().name();
        return r;
    }
}
