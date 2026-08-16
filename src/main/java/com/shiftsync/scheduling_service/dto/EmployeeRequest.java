package com.shiftsync.scheduling_service.dto;

import lombok.Data;

@Data
public class EmployeeRequest {
    private Long locationId;
    private String name;
    private String email;
    private String role;
}
