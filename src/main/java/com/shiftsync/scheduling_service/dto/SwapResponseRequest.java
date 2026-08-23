package com.shiftsync.scheduling_service.dto;

import lombok.Data;

@Data
public class SwapResponseRequest {
    private Long employeeId; // must match the swap request's targetEmployee, validated server-side
    private String comment;
    private boolean willingToCover; // true = "I can cover this", false = "I can't, sorry"
}
