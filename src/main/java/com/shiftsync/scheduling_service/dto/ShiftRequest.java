package com.shiftsync.scheduling_service.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShiftRequest {
    private Long locationId;
    private Long employeeId; // nullable — omit for an OPEN shift
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
