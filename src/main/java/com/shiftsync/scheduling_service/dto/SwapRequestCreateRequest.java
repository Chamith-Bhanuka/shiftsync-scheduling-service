package com.shiftsync.scheduling_service.dto;

import lombok.Data;

@Data
public class SwapRequestCreateRequest {
    private Long shiftId;
    private Long requestingEmployeeId;
    private Long targetEmployeeId;
}
